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
import java.io.IOException;

import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;

import parchment.manuscript.Manuscript;
import quill.textbase.Folio;

/**
 * Load then serialize a Parchment format document. This is basically so that
 * if you've had to manually mess with the document sources you can
 * re-normalize.
 * 
 * @author Andrew Cowie
 */
// Forked from quill.client.Render which was forked from quill.client.Quill
public class Format
{
    private static String filename;

    private static Manuscript manuscript;

    private static Folio folio;

    public static void main(String[] args) throws Exception {
        try {
            initializeUserInterface(args);
            parseCommandLine(args);
            loadDocument();
            serializeDocument();
        } catch (SafelyTerminateException ste) {
            // quietly supress
            return;
        }
    }

    /*
     * We do need to initialize java-gnome. We don't need to initialize the
     * Quill UI.
     */
    static void initializeUserInterface(String[] args) {
        Glib.setProgramName("quill");
        Gtk.init(args);
    }

    /**
     * Parse arguments from command line. See
     * {@link Quill#parseCommandLine(String[])} for a discussion of how hard
     * this is.
     */
    /*
     * TODO parse arguments properly here.
     */
    static void parseCommandLine(String[] args) throws Exception {
        if (args.length == 1) {
            filename = args[0];
        } else {
            System.err.println("ERROR: Please supply one filename to reformat.");
            throw new SafelyTerminateException();
        }
    }

    static void loadDocument() throws Exception {
        final Manuscript attempt;

        try {
            attempt = new Manuscript(filename);
            attempt.checkFilename();
            manuscript = attempt;
            folio = manuscript.loadDocument();
        } catch (FileNotFoundException fnfe) {
            System.err.println("ERROR: File not found?" + "\n" + fnfe.getMessage());
            throw new SafelyTerminateException();
        }
    }

    static void serializeDocument() throws IOException {
        manuscript.saveDocument(folio);
    }
}
