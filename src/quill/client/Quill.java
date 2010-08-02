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
package quill.client;

import java.io.FileNotFoundException;

import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;

import parchment.format.Manuscript;
import quill.textbase.Folio;
import quill.ui.UserInterface;

/**
 * Main execution entry point for the Quill what-you-see-is-what-you-need
 * editor of Quack XML documents, using the Parchment rendering engine to
 * produce printable output.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO the ui singleton really needs to move somewhere else, or better yet be
 * passed along when instantiating things.
 */
public class Quill
{
    private static UserInterface ui;

    public static void main(String[] args) throws Exception {
        try {
            initializeUserInterface(args);
            parseCommandLine(args);
            runUserInterface();
        } catch (SafelyTerminateException ste) {
            // quietly supress
            return;
        }
    }

    static void initializeUserInterface(String[] args) {
        Glib.setProgramName("quill");
        Gtk.init(args);

        ui = new UserInterface();
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
        Manuscript manuscript;
        Folio folio;
        PrimaryWindow primary;

        try {
            manuscript = new Manuscript(filename);
        } catch (ImproperFilenameException ife) {
            ui.error(ife); // change to info + exit
            throw new SafelyTerminateException();
        }

        try {
            manuscript.checkFilename();
            folio = manuscript.loadDocument();
        } catch (FileNotFoundException fnfe) {
            manuscript = new Manuscript(filename);
            folio = manuscript.createDocument();
        } catch (RecoveryFileExistsException rfee) {
            ui.warning(rfee);
            folio = manuscript.loadDocument();
        }

        primary = ui.createPrimaryWindow();
        primary.displayDocument(folio);
    }

    static void loadDocumentBlank() {
        final Manuscript manuscript;
        final Folio folio;

        // sets active
        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        // there a cleaner way to do this?
        ui.displayDocument(manuscript, folio);
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
            ui.emergencySave();
        }
    }

    public static UserInterface getUserInterface() {
        if (ui == null) {
            throw new Error("Not yet initialized");
        }
        return ui;
    }

    public static void setUserInterface(UserInterface instance) {
        if (ui != null) {
            throw new Error("UserInterface already initialized");
        }
        ui = instance;
    }
}
