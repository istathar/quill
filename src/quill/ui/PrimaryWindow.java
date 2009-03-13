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
import org.gnome.gdk.WindowState;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Notebook;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.pango.FontDescription;

import quill.textbase.Series;

import static quill.client.Quill.ui;

/**
 * The main application window. Single instance, probably.
 * 
 * @author Andrew Cowie
 */
class PrimaryWindow extends Window
{
    private Window window;

    private VBox top;

    private HBox two;

    private Notebook left;

    private Notebook right;

    private ComponentEditorWidget editor;

    private HelpWidget help;

    private PreviewWidget preview;

    /**
     * The Components currently being represented by this PrimaryWindow
     */
    Series series;

    PrimaryWindow() {
        super();
        setupWindow();
        setupEditorSide();
        setupPreviewSide();
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

        top = new VBox(false, 0);
        window.add(top);

        two = new HBox(true, 6);
        top.packStart(two, true, true, 0);
    }

    private void setupEditorSide() {
        left = new Notebook();
        left.setShowTabs(false);
        left.setShowBorder(false);

        editor = new ComponentEditorWidget();
        left.insertPage(editor, null, 0);

        two.add(left);
    }

    private void setupPreviewSide() {
        right = new Notebook();
        right.setShowTabs(false);
        right.setShowBorder(false);

        preview = new PreviewWidget();
        right.add(preview);

        help = new HelpWidget();
        right.add(help);

        two.add(right);
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

                if (key == Keyval.F1) {
                    switchToHelp();
                    return true;
                } else if (key == Keyval.F2) {
                    switchToPreview();
                    return true;
                }
                // ...
                else if (key == Keyval.F11) {
                    toggleFullscreen();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Change the user interface from full screen to normal and back again.
     * Quill is designed to work maximized, but you get an even better
     * experience if you fullscreen.
     */
    void toggleFullscreen() {
        if (window.getWindow().getState().contains(WindowState.FULLSCREEN)) {
            window.setFullscreen(false);
        } else {
            window.setFullscreen(true);
        }
    }

    /**
     * Change the left side to show the chapter editor.
     */
    void switchToEditor() {
        left.setCurrentPage(0);
    }

    /**
     * Change the right side to show the help pane.
     */
    void switchToHelp() {
        right.setCurrentPage(1);
    }

    /**
     * Change the right side to show the preview display.
     */
    void switchToPreview() {
        right.setCurrentPage(0);
    }

    void loadDocument(Series series) {
        this.series = series;

        editor.initializeSeries(series);
    }
}
