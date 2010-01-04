/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.ui;

/*
 * Adapted from code in Compendium.
 */

import java.io.FileNotFoundException;
import java.io.IOException;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.PdfSurface;
import org.freedesktop.cairo.Surface;
import org.freedesktop.enchant.Dictionary;
import org.freedesktop.enchant.Enchant;
import org.gnome.gdk.EventOwnerChange;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonsType;
import org.gnome.gtk.Clipboard;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.ErrorMessageDialog;
import org.gnome.gtk.FileChooserDialog;
import org.gnome.gtk.FileFilter;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.InfoMessageDialog;
import org.gnome.gtk.MessageDialog;
import org.gnome.gtk.MessageType;
import org.gnome.gtk.PaperSize;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Unit;
import org.gnome.pango.FontDescription;

import parchment.render.RenderEngine;
import parchment.render.ReportRenderEngine;
import quill.client.ApplicationException;
import quill.client.RecoveryFileExistsException;
import quill.client.SafelyTerminateException;
import quill.textbase.Change;
import quill.textbase.DataLayer;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.Series;
import quill.textbase.Span;

import static org.gnome.gtk.FileChooserAction.OPEN;
import static org.gnome.gtk.FileChooserAction.SAVE;
import static quill.textbase.Span.createSpan;

public class UserInterface
{
    private DataLayer data;

    PrimaryWindow primary;

    Dictionary dict;

    public UserInterface(DataLayer data) {
        loadImages();
        loadFonts();
        setupUndoCapability(data);
        setupApplication();
        setupWindows();
        hookupExternalClipboard();
        loadDictionary();
    }

    private void setupUndoCapability(final DataLayer layer) {
        data = layer;
    }

    private void setupWindows() {
        primary = new PrimaryWindow();
    }

    private void loadImages() {
        try {
            images.quill = new Pixbuf("share/pixmaps/quill-and-parchment.png"); // 48x48
        } catch (FileNotFoundException fnfe) {
            System.err.println("Icon file not found: " + fnfe.getMessage());
        }
    }

    private void loadFonts() {
        fonts.serif = new FontDescription("Deja Vu Serif, 11");
        fonts.sans = new FontDescription("Deja Vu Sans, 11");
        fonts.mono = new FontDescription("Deja Vu Sans Mono, 11");
    }

    private void setupApplication() {
        Gtk.setDefaultIcon(images.quill);
    }

    /**
     * Terminate the user interface (and, by returning to main(), precipiate
     * the application to terminate).
     */
    public void shutdown() {
        Gtk.mainQuit();
    }

    private Clipboard clipboard;

    /**
     * Do we [think] we're the owner of the clipboard?
     */
    private boolean owner;

    /**
     * When text is cut or copied out, it will be cached here.
     */
    private Extract stash;

    private void hookupExternalClipboard() {
        stash = null;
        clipboard = Clipboard.getDefault();

        /*
         * This gets hit whenever the clipboard is written to. In our case, we
         * toggle the owner state variable to true when we know we're about to
         * programatically set the clipboard; this handler gets called once,
         * and we can ignore it. Any other receipt of this event and we have
         * to get the text from the system.
         */

        clipboard.connect(new Clipboard.OwnerChange() {
            public void onOwnerChange(Clipboard source, EventOwnerChange event) {
                final Span span;
                final String str;

                if (owner) {
                    owner = false;
                } else {
                    str = clipboard.getText();
                    if (str == null) {
                        /*
                         * If the data in the system clipboard was put there
                         * in a a form other than plain text we get null back.
                         */
                        return;
                    }
                    span = createSpan(str, null);
                    stash = Extract.create(span);
                }
            }
        });
    }

    /**
     * Get the Span(s) that are currently in the clipboard. This will be rich
     * Spans with Markup if the cut/copy operation was done within our
     * application, otherwise it will be a plain text.
     */
    Extract getClipboard() {
        return stash;
    }

    /**
     * Put the extracted text into the system clipboard.
     */
    void setClipboard(Extract extract) {
        owner = true;
        stash = extract;
        clipboard.setText(extract.getText());
    }

    /**
     * Initiate display of the given document.
     */
    /*
     * For now this starts at the first component, but presumably we want to
     * record somewhere what the last edited point was and start there
     * instead. We also need to instruct the PrimaryWindow about what
     * navigation options it can offer. Which means maybe we should pass it
     * the Folio?
     */
    public void displayDocument(Folio folio) {
        final Series series;

        if (folio.size() == 0) {
            throw new IllegalStateException();
        }

        series = folio.get(0);
        primary.displaySeries(data, series);
    }

    void saveAs() throws SaveCancelledException {
        requestFilename();
        saveDocument();
    }

    /**
     * This WILL set the filename in the DataLayer, unless cancelled.
     */
    /*
     * This is a bit messy. There is confusion of responsibilities here
     * between who owns the filename, who is responsible for carrying out IO,
     * and who drives the process.
     */
    void requestFilename() throws SaveCancelledException {
        final FileChooserDialog dialog;
        String filename;
        ResponseType response;

        dialog = new FileChooserDialog("Save As...", primary, SAVE);

        while (true) {
            response = dialog.run();
            dialog.hide();

            if (response != ResponseType.OK) {
                throw new SaveCancelledException();
            }

            filename = dialog.getFilename();

            try {
                data.setFilename(filename);
                return;
            } finally {
                // try again
            }
        }
    }

    /**
     * Cause the document to be saved. Returns false on error or if cancelled.
     */
    void saveDocument() throws SaveCancelledException {
        MessageDialog dialog;
        String filename;

        filename = data.getFilename();

        if (filename == null) {
            requestFilename(); // throws if user cancels
        }

        try {
            data.saveDocument();
        } catch (IllegalStateException ise) {
            dialog = new ErrorMessageDialog(primary, "Save failed",
                    "There is a problem in the structure or data of your document: " + ise.getMessage());
            dialog.run();
            dialog.hide();
        } catch (IOException ioe) {
            dialog = new ErrorMessageDialog(primary, "Save failed", ioe.getMessage());
            dialog.setSecondaryUseMarkup(true);
            dialog.run();
            dialog.hide();
        }
    }

    /**
     * Cause the document to be printed.
     */
    /*
     * Passing a target filename in from here is either correct, or should be
     * sourced from the DataLayer. The code driving the renderer probably
     * shouldn't be here. Should it be in RenderEngine instead? Improving this
     * will also have to wait on our establishing a proper abstraction for
     * documents as a whole, containing settings relating to publishing. This
     * code copied from what is presently our command line driven
     * RenderToPrintHarness.
     */
    void printDocument() {
        final String parentdir, fullname, basename, targetname;
        MessageDialog dialog;
        final Context cr;
        final Surface surface;
        final Folio folio;
        final PaperSize paper;
        final RenderEngine engine;

        try {
            paper = PaperSize.A4;

            fullname = data.getFilename();
            if (fullname == null) {
                dialog = new InfoMessageDialog(primary, "Set filename first",
                        "You can't print the document (to PDF) until you've set the filename of this document. "
                                + "Choose <b>Save As...</b>, then come back and try again!");
                dialog.setSecondaryUseMarkup(true);
                dialog.run();
                dialog.hide();
                return;
            }

            /*
             * Get the basename of the current [save] filename, then
             * instantiate the Cairo Surface we're going to be drawing to with
             * that basename.pdf as the target.
             */
            parentdir = data.getDirectory();
            basename = data.getBasename();
            targetname = parentdir + "/" + basename + ".pdf";

            surface = new PdfSurface(targetname, paper.getWidth(Unit.POINTS),
                    paper.getHeight(Unit.POINTS));
            cr = new Context(surface);

            folio = data.getActiveDocument();

            // HARDCODE
            engine = new ReportRenderEngine(paper, data, folio.get(0));
            engine.render(cr);

            surface.finish();
        } catch (IOException ioe) {
            dialog = new ErrorMessageDialog(primary, "Print failed",
                    "There's some kind of I/O problem: " + ioe.getMessage());
            dialog.run();
            dialog.hide();
        }
    }

    /**
     * Pick the latest Change off the ChangeStack, and then do something with
     * it
     */
    void undo() {
        final Change change;

        change = data.undo();

        if (change == null) {
            return;
        }

        primary.reverse(change);
    }

    /**
     * Grab the Change that was most recently undone, and redo it.
     */
    void redo() {
        final Change change;

        change = data.redo();

        if (change == null) {
            return;
        }

        primary.affect(change);
    }

    /**
     * Cause a Change to be forward applied to the in-memory representation.
     */
    void apply(Change change) {
        data.apply(change);
        primary.affect(change);
    }

    /**
     * Change the UI to report a (fatal) error condition.
     */
    /*
     * TODO in development, frankly, I just need the stack trace. So we're
     * actually skipping calling this crash dialog for now. Maybe we should
     * attempt an autosave here, though?
     */
    public void error(Exception e) {
        Dialog d;

        if (primary != null) {
            primary.hide();
        }
        e.printStackTrace();

        d = new ErrorMessageDialog(null, "Problem", e.getMessage());
        d.run();
        d.hide();

        System.exit(1);
    }

    public void focusEditor() {
        primary.grabFocus();
    }

    private void loadDictionary() {
        Enchant.init();
        dict = Enchant.requestDictionary("en_CA");
    }

    public void warning(ApplicationException ae) throws SafelyTerminateException {
        final MessageDialog dialog;
        final Button cancel, ok;
        final ResponseType response;

        if (ae instanceof RecoveryFileExistsException) {
            dialog = new MessageDialog(primary, true, MessageType.WARNING, ButtonsType.NONE,
                    "Crash recovery file exists");
            cancel = new Button();
            cancel.setImage(new Image(Stock.CANCEL, IconSize.BUTTON));
            cancel.setLabel("Cancel loading and Quit");
            dialog.addButton(cancel, ResponseType.CANCEL);

            ok = new Button();
            ok.setImage(new Image(Stock.OK, IconSize.BUTTON));
            ok.setLabel("Load original anyway");
            dialog.addButton(ok, ResponseType.OK);

            dialog.setSecondaryText("A recovery file exists:" + "\n<tt>" + ae.getMessage() + "</tt>\n\n"
                    + "It <i>may</i> contain what you were working on before Quill crashed. "
                    + "You should quit and review it against your actual document. Once you're sure "
                    + "the rescue file doesn't contain anything you need, you can delete it." + "\n\n"
                    + "Be warned that if the program crashes again, we will <i>not</i> overwrite "
                    + "the existing recovery file.", true);

            dialog.showAll();
            response = dialog.run();
            dialog.hide();

            if (response != ResponseType.OK) {
                /*
                 * FUTURE This will need to change if we support editing
                 * multiple documents in one Quill process.
                 */
                throw new SafelyTerminateException();
            }
        }
    }

    /**
     * Ask the user if they want to save the document before closing the app
     * or discarding it by opening a new one.
     */
    void saveIfModified() throws SaveCancelledException {
        final MessageDialog dialog;
        String filename;
        final ResponseType response;
        final Button discard, cancel, ok;

        if (!data.isModified()) {
            return;
        }

        // TODO change to document title?
        filename = data.getFilename();
        if (filename == null) {
            filename = "(untitled)";
        }

        dialog = new MessageDialog(primary, true, MessageType.WARNING, ButtonsType.NONE,
                "Save Document?");
        dialog.setSecondaryText("The current document" + "\n<tt>" + filename + "</tt>\n"
                + "has been modified. Do you want to save it first?", true);

        discard = new Button();
        discard.setImage(new Image(Stock.DELETE, IconSize.BUTTON));
        discard.setLabel("Discard changes");
        dialog.addButton(discard, ResponseType.CLOSE);

        cancel = new Button();
        cancel.setImage(new Image(Stock.CANCEL, IconSize.BUTTON));
        cancel.setLabel("Return to editor");
        dialog.addButton(cancel, ResponseType.CANCEL);

        ok = new Button();
        ok.setImage(new Image(Stock.SAVE, IconSize.BUTTON));
        ok.setLabel("Yes, save");
        dialog.addButton(ok, ResponseType.OK);

        dialog.showAll();
        dialog.setTitle("Document modified!");
        ok.grabFocus();

        response = dialog.run();
        dialog.hide();

        if (response == ResponseType.OK) {
            saveDocument();
        } else if (response == ResponseType.CLOSE) {
            return;
        } else {
            throw new SaveCancelledException();
        }
    }

    /**
     * Open a new chapter in the editor, replacing the current one.
     */
    void openDocument() {
        final FileChooserDialog dialog;
        final FileFilter filter;
        final ErrorMessageDialog error;
        String filename;
        ResponseType response;
        final Folio folio;

        dialog = new FileChooserDialog("Open file...", primary, OPEN);

        filter = new FileFilter();
        filter.setName("Documents");
        filter.addPattern("*.xml");
        dialog.addFilter(filter);

        response = dialog.run();
        dialog.hide();

        if (response != ResponseType.OK) {
            return;
        }

        filename = dialog.getFilename();

        try {
            data.loadDocument(filename);
        } catch (Exception e) {
            error = new ErrorMessageDialog(
                    primary,
                    "Failed to load document!",
                    "Problem encountered when attempting to load:"
                            + "\n<tt>"
                            + filename
                            + "</tt>\n\n"
                            + "Worse, it wasn't something we were expecting. Here's the internal message, which might help a developer fix the problem:\n\n<tt>"
                            + e.getClass().getSimpleName() + "</tt>:\n<tt>" + e.getMessage() + "</tt>");
            error.setSecondaryUseMarkup(true);

            error.run();
            error.hide();
            return;
        }
        folio = data.getActiveDocument();
        this.displayDocument(folio);
    }
}

class images
{
    static Pixbuf quill;
}

class fonts
{
    static FontDescription sans;

    static FontDescription serif;

    static FontDescription mono;
}
