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

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;

public class UserInterface
{
    PrimaryWindow primary;

    public UserInterface() {
        loadImages();
        setupApplication();
        setupWindows();
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
}

class images
{
    static Pixbuf quill;

}
