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

import java.io.FileNotFoundException;

import org.gnome.gdk.EventOwnerChange;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Clipboard;
import org.gnome.gtk.Gtk;
import org.gnome.pango.FontDescription;

import quill.textbase.Extract;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.StringSpan;

import static quill.textbase.Text.extractFor;

public class UserInterface
{
    PrimaryWindow primary;

    public UserInterface() {
        loadImages();
        loadFonts();
        setupApplication();
        setupWindows();
        hookupExternalClipboard();
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
                    span = new StringSpan(str, null);
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

    public void loadDocument(Series series) {
        primary.loadDocument(series);
    }

    public void saveDocument() {

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
