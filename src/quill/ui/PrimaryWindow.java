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
import org.gnome.gtk.Allocation;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Notebook;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.pango.FontDescription;

import quill.textbase.Change;
import quill.textbase.DataLayer;
import quill.textbase.Origin;
import quill.textbase.Segment;
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

    private OutlineWidget outline;

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
        window.setTitle("Quill");

        desc = new FontDescription("Deja Vu Serif, 11");
        window.modifyFont(desc);

        top = new VBox(false, 0);
        window.add(top);

        two = new HBox(true, 0);
        top.packStart(two, true, true, 0);
    }

    private void setupEditorSide() {
        left = new Notebook();
        left.setShowTabs(false);
        left.setShowBorder(false);
        left.setSizeRequest(640, -1);

        editor = new ComponentEditorWidget();
        left.insertPage(editor, null, 0);

        two.packStart(left, true, true, 0);
    }

    private void setupPreviewSide() {
        right = new Notebook();
        right.setShowTabs(false);
        right.setShowBorder(false);

        preview = new PreviewWidget();
        right.add(preview);

        help = new HelpWidget();
        right.add(help);

        outline = new OutlineWidget();
        right.add(outline);

        two.packStart(right, false, false, 0);
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
                mod = event.getState();

                if (mod == ModifierType.NONE) {
                    if (key == Keyval.F1) {
                        switchToHelp();
                        return true;
                    } else if (key == Keyval.F2) {
                        switchToPreview();
                        return true;
                    } else if (key == Keyval.F3) {
                        switchToOutline();
                        return true;
                    }

                    if ((key == Keyval.F4) || (key == Keyval.F5) || (key == Keyval.F6)
                            || (key == Keyval.F7) || (key == Keyval.F8) || (key == Keyval.F9)
                            || (key == Keyval.F10)) {
                        // nothing yet
                        return true;
                    }

                    else if (key == Keyval.F11) {
                        toggleFullscreen();
                        return true;
                    } else if (key == Keyval.F12) {
                        toggleRightSide();
                        return true;
                    }
                } else if (mod == ModifierType.CONTROL_MASK) {
                    if (key == Keyval.p) {
                        ui.printDocument();
                        return true;
                    }
                    if (key == Keyval.s) {
                        ui.saveDocument();
                        return true;
                    } else if (key == Keyval.y) {
                        ui.redo();
                        return true;
                    } else if (key == Keyval.z) {
                        ui.undo();
                        return true;
                    }
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

    private boolean showingRightSide = true;

    /*
     * Not a documented public feature. This code is here only so we can
     * demonstrate just how hard getting the user experience correct for this
     * is. The correct end result would be keeping the vertical height of
     * previously set, and probably horizontal width as well. What is really
     * bad is that if you return to maximized with the right hand side turned
     * off suddenly the editor is super wide, and that's a horrible
     * experience.
     */
    private void toggleRightSide() {
        final Allocation alloc;

        if (showingRightSide) {
            alloc = left.getAllocation();
            right.hide();
            window.setMaximize(false);
            window.resize(alloc.getWidth(), 720);
            showingRightSide = false;
        } else {
            right.show();
            window.setMaximize(true);
            showingRightSide = true;
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
     * Change the right side to show the preview display, and ensure it is up
     * to date.
     */
    void switchToPreview() {
        right.setCurrentPage(0);
        preview.queueDraw();
    }

    /**
     * Change the right side to show the outline navigator.
     */
    void switchToOutline() {
        right.setCurrentPage(2);
        outline.renderSeries(series);
    }

    /**
     * Adjust the left side editor to show the supplied vertical location.
     */
    /*
     * This probably doesn't need to be a UI function; an EditorTextView
     * should know or be able to find out its parent and simply call its
     * parent's ensureVisible() method.
     */
    void scrollEditorToShow(int from, int height) {
        editor.ensureVisible(from, height);
    }

    /**
     * Show the nominated Series in this PrimaryWindow
     */
    /*
     * FUTURE considerations: when we start dealing in multiple chapters,
     * we'll want to be able to navigate between them, which means this UI
     * will need to be told what other Series are available. So be prepared to
     * change this to accepting a Folio.
     */
    void displaySeries(DataLayer data, Series series) {
        this.series = series;

        editor.initializeSeries(data, series);
        preview.renderSeries(data, series);
        outline.renderSeries(series);
        this.updateTitle();
    }

    /**
     * Set or reset the Window title based on the text of the first Segment of
     * the currently dipslayed Series (which will be the ComponentSegment
     * leading this chapter with the chapter title).
     */
    /*
     * There is a HIG question here, as to whether the application name should
     * be in the window title. Since Open Office, Firefox, Evolution, Eclipse,
     * and Inkscape all put their titles in, we will too. In always having
     * "Quill" in the title, we do manage to avoid the transient ugliness that
     * occurs when you are just entering the chapter title for the first time
     * and have a window title with only one letter in it as you type.
     */
    void updateTitle() {
        final Segment first;
        final String title;
        final String str;

        first = series.get(0);
        title = first.getText().toString();

        if ((title == null) || (title.equals(""))) {
            str = "Quill";
        } else {
            str = title + " - Quill";
        }

        super.setTitle(str);
    }

    void affect(Change change) {
        editor.affect(change);
    }

    void reverse(Change change) {
        editor.reverse(change);
    }

    public void grabFocus() {
        editor.grabFocus();
    }

    Origin getCursor() {
        return editor.getCursor();
    }
}
