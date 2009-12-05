/*
 * Quill.java, from the Quill and Parchment WYSIWYN document editor.
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License, version
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
package quill.client;

import java.io.FileNotFoundException;

import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;

import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.ui.UserInterface;

/**
 * Main execution entry point for the Quill what-you-see-is-what-you-need
 * editor of Quack XML documents, using the Parchment rendering engine to
 * produce printable output.
 * 
 * @author Andrew Cowie
 */
public class Quill
{
    public static UserInterface ui;

    private static DataLayer data;

    public static void main(String[] args) throws Exception {
        try {
            initializeDataLayer();
            initializeUserInterface(args);
            parseCommandLine(args);
            runUserInterface();
        } catch (SafelyTerminateException ste) {
            // quietly supress
            return;
        }
    }

    static void initializeDataLayer() {
        data = new DataLayer();
    }

    static void initializeUserInterface(String[] args) {
        Glib.setProgramName("quill");
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
            data.checkDocument(filename);
            data.loadDocument(filename);
        } catch (FileNotFoundException fnfe) {
            data.createDocument();
            data.setFilename(filename);
        } catch (RecoveryFileExistsException rfee) {
            ui.warning(rfee);
            data.loadDocument(filename);
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
        try {
            ui.focusEditor();
            Gtk.main();
        } catch (Throwable t) {
            t.printStackTrace();
            data.emergencySave();
        }
    }
}
