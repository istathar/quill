/*
 * UserInterface.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 * 
 * Adapted from code in project compendium.
 */
package quill.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.freedesktop.enchant.Dictionary;
import org.freedesktop.enchant.Enchant;
import org.gnome.gdk.EventOwnerChange;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Clipboard;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.ErrorMessageDialog;
import org.gnome.gtk.FileChooserDialog;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.ResponseType;
import org.gnome.pango.FontDescription;

import quill.textbase.Change;
import quill.textbase.DataLayer;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.Span;

import static org.gnome.gtk.FileChooserAction.SAVE;
import static quill.textbase.Span.createSpan;
import static quill.textbase.TextChain.extractFor;

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
        Gtk.setProgramName("quill");
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
                    stash = extractFor(span);
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
    void setClipboard(Extract range) {
        owner = true;
        stash = range;
        clipboard.setText(range.getText());
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
        if (folio.size() == 0) {
            throw new IllegalStateException();
        }

        primary.displaySeries(folio.get(0));
    }

    void saveAs() {
        requestFilename();

        saveDocument();
    }

    /**
     * This WILL set the filename in the DataLayer. However, you can check the
     * return value to find out whether or not something useful happened.
     */
    /*
     * This is a bit messy. There is confusion of responsibilities here
     * between who owns the filename, who is responsible for carrying out IO,
     * and who drives the process.
     */
    File requestFilename() {
        final FileChooserDialog dialog;
        String filename;
        ResponseType response;
        File result;

        dialog = new FileChooserDialog("Save As...", primary, SAVE);

        while (true) {
            response = dialog.run();
            dialog.hide();

            if (response != ResponseType.OK) {
                return null;
            }

            filename = dialog.getFilename();

            try {
                result = data.setFilename(filename);
                if (result != null) {
                    return result;
                }
            } finally {
                // try again
            }
        }
    }

    void saveDocument() {
        final Dialog dialog;
        File target;

        target = data.getFilename();

        if (target == null) {
            target = requestFilename();
            if (target == null) {
                return; // cancelled by user
            }
        }

        try {
            data.saveDocument();
        } catch (IOException ioe) {
            dialog = new ErrorMessageDialog(primary, "Save failed", "There's some kind of I/O problem: "
                    + ioe.getMessage());
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
