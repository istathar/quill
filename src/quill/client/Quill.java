/*
 * Quill.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.client;

import java.io.FileNotFoundException;

import org.gnome.gtk.Gtk;

import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.ui.UserInterface;

public class Quill
{
    public static UserInterface ui;

    private static DataLayer data;

    public static void main(String[] args) throws Exception {
        initializeDataLayer();
        initializeUserInterface(args);
        parseCommandLine(args);
        runUserInterface(); // blocks
    }

    static void initializeDataLayer() {
        data = new DataLayer();
    }

    static void initializeUserInterface(String[] args) {
        Gtk.init(args);

        ui = new UserInterface(data);
    }

    /*
     * TODO parsing problems are going to be insanely difficult to present to
     * the user. And this is before the main loop is running.
     * 
     * TODO parse arguments properly here.
     */
    static void parseCommandLine(String[] args) throws Exception {
        if (args.length > 0) {
            loadDocumentFile(args[0]);
        } else {
            loadDocumentBlank();
        }
    }

    static void loadDocumentFile(String filename) throws Exception {
        final Folio folio;

        try {
            data.loadDocument(filename);
        } catch (FileNotFoundException fnfe) {
            data.createDocument();
            data.setFilename(filename);
        }
        folio = data.getActiveDocument();
        ui.displayDocument(folio);
    }

    static void loadDocumentBlank() {
        final Folio folio;

        // sets active
        data.createDocument();

        // there a cleaner way to do this?
        folio = data.getActiveDocument();
        ui.displayDocument(folio);
    }

    /**
     * Run the GTK main loop. This call blocks.
     */
    static void runUserInterface() {
        Gtk.main();
    }
}
