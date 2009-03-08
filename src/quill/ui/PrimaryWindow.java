/*
 * PrimaryWindow.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gdk.Event;
import org.gnome.gdk.EventKey;
import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.pango.FontDescription;

import static quill.client.Quill.ui;

/**
 * The main application window. Single instance, probably.
 * 
 * @author Andrew Cowie
 */
class PrimaryWindow extends Window
{
    private Window window;

    PrimaryWindow() {
        super();
        setupWindow();
        hookupDefaultKeyhandlers();
        hookupWindowManagement();
        initialPresentation();
    }

    private void setupWindow() {
        final FontDescription desc;

        window = this;
        window.setMaximize(true);

        desc = new FontDescription("Deja Vu Serif, 11");
        window.modifyFont(desc);
    }

    private void initialPresentation() {
        window.showAll();
        window.present();
    }

    private void hookupWindowManagement() {
        window.connect(new Window.DeleteEvent() {
            public boolean onDeleteEvent(Widget source, Event event) {
                ui.shutdown();
                return false;
            }
        });
    }

    private void hookupDefaultKeyhandlers() {
        window.connect(new Widget.KeyPressEvent() {
            public boolean onKeyPressEvent(Widget source, EventKey event) {
                final Keyval key;
                final ModifierType mod;

                key = event.getKeyval();

                /*
                 * Let default keybindings handle cursor movement keys and for
                 * a few other special keys we don't need to handle.
                 */

                if ((key == Keyval.F1) || (key == Keyval.F10)) {
                    System.out.println(key.toString());
                }

                if (key == Keyval.F11) {
                    ui.toggleFullscreen();
                    return true;
                }

                return false;
            }
        });
    }
}
