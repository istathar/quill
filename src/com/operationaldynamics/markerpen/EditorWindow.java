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
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextMark;
import org.gnome.gtk.TextTag;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.pango.Style;
import org.gnome.pango.Weight;

class EditorWindow extends Window
{
    private Window window;

    private VBox top;

    private TextBuffer buffer;

    private TextView view;

    EditorWindow() {
        super();

        setupWindow();
        setupEditor();

        installKeybindings();

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

    private TextTag italics;

    private TextTag bold;

    private void setupEditor() {
        buffer = new TextBuffer();

        view = new TextView(buffer);
        top.packStart(view);

        italics = new TextTag();
        italics.setStyle(Style.ITALIC);

        bold = new TextTag();
        bold.setWeight(Weight.BOLD);
    }

    private void installKeybindings() {
        view.connect(new KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;
                final ModifierType mod;

                key = event.getKeyval();
                mod = event.getState();

                if (mod == ModifierType.CONTROL_MASK) {
                    if (key == Keyval.i) {
                        applyFormat(italics);
                        return true;
                    } else if (key == Keyval.b) {
                        applyFormat(bold);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void applyFormat(TextTag format) {
        final TextMark selectionBound, insert;

        selectionBound = buffer.getSelectionBound();
        insert = buffer.getInsert();

        buffer.applyTag(format, selectionBound.getIter(), insert.getIter());
    }
}
