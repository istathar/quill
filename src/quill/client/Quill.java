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

import org.gnome.gtk.Gtk;

import quill.textbase.DataLayer;
import quill.ui.UserInterface;

public class Quill
{
    public static UserInterface ui;

    public static DataLayer data;

    public static void main(String[] args) {
        initializeDataLayer();
        initializeUserInterface(args);
        runUserInterface(); // blocks
    }

    static void initializeDataLayer() {
        data = new DataLayer();
    }

    static void initializeUserInterface(String[] args) {
        Gtk.init(args);

        ui = new UserInterface();
    }

    /**
     * Run the GTK main loop. This call blocks.
     */
    static void runUserInterface() {
        Gtk.main();
    }
}
