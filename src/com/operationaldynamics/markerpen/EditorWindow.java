/*
 * EditorWindow.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.markerpen;

import org.gnome.gdk.Event;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;

class EditorWindow extends Window
{
    private Window window;

    private VBox top;

    private TextView view;

    EditorWindow() {
        super();

        setupWindow();
        setupEditor();

        completeWindow();
    }

    private void setupWindow() {
        window = this;
        window.setDefaultSize(200, 300);

        top = new VBox(false, 6);
        window.add(top);

        window.connect(new DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                Gtk.mainQuit();
                return false;
            }
        });
    }

    private void completeWindow() {
        window.showAll();
    }

    private void setupEditor() {
        view = new TextView();
        top.packStart(view);
    }

}
