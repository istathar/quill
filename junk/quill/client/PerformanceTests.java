/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Test;

import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.ui.UserInterface;

import static quill.client.Quill.ui;

/**
 * Simple harness to time bringing up the Quill user interface and then load a
 * document.
 * 
 * @author Andrew Cowie
 */
/*
 * This should adapt in time to become a comprehensive simulation of user
 * activity, but that's non trivial.
 */
public class PerformanceTests
{
    private static DataLayer data;

    public static void main(String[] args) throws Exception {
        long start, finish, duration;

        start = System.currentTimeMillis();

        try {
            initializeDataLayer();
            initializeUserInterface(args);
            loadDocumentFile("tests/AFewNotesOnTheCulture.xml");
            Test.cycleMainLoop();
        } catch (SafelyTerminateException ste) {
            // ignore
        }

        finish = System.currentTimeMillis();
        duration = finish - start;

        System.out.printf("%d.%03d\n", duration / 1000, duration % 1000);
    }

    static void initializeDataLayer() {
        data = new DataLayer();
    }

    static void initializeUserInterface(String[] args) {
        Glib.setProgramName("quill-timing");
        Gtk.init(args);

        /*
         * Is there a better way to do this singleton?
         */
        ui = new UserInterface(data);
    }

    static void loadDocumentFile(String filename) throws Exception {
        final Folio folio;

        data.checkDocument(filename);
        data.loadDocument(filename);

        folio = data.getActiveDocument();
        ui.displayDocument(folio);
    }
}
