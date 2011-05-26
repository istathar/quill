/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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
package quill.ui;

import org.gnome.gdk.EventFocus;
import org.gnome.gdk.RGBA;
import org.gnome.gtk.Align;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Entry;
import org.gnome.gtk.EventBox;
import org.gnome.gtk.Grid;
import org.gnome.gtk.Label;
import org.gnome.gtk.StateFlags;
import org.gnome.gtk.Widget;

import quill.textbase.Segment;

/**
 * An Editor wrapper that has a label and a body. For list items, and notes
 * &amp; references.
 * 
 * @author Andrew Cowie
 */
abstract class ListitemBox extends EventBox implements Editor
{
    /**
     * Width of label (or corresponding indent).
     */
    private static final int WIDTH = 30;

    private final EventBox eb;

    private final Grid grid;

    /*
     * This is a terrible name, but what else are we supposed to use?
     */
    private ListitemEntry label;

    private EditorTextView body;

    private final SeriesEditorWidget parent;

    ListitemBox(final SeriesEditorWidget parent) {
        super();
        eb = this;

        grid = new Grid();

        eb.add(grid);
        eb.overrideBackground(StateFlags.NORMAL, RGBA.WHITE);

        /*
         * This is to prevent the annoying behaviour of the Entry selecting
         * its entire text when focus passes through. Bit of a workaround, but
         * doing this down in ListitemEntry seems not to work.
         */

        eb.setCanFocus(true);
        eb.connect(new Widget.FocusInEvent() {
            public boolean onFocusInEvent(Widget source, EventFocus event) {
                label.setPosition(-1);
                label.selectRegion(0, 0);
                return false;
            }
        });

        this.parent = parent;
    }

    /**
     * Put a label in the normal bullet or endnote number. Labels are right
     * aligned. Top <code>padding</code> is for pushing the bullet or ordinal
     * down to line up with the text's baseline.
     */
    void setupLabelSide(final ListitemEntry entry, final int left, final int top) {

        entry.setSizeRequest(WIDTH, -1);
        entry.setAlignment(Alignment.LEFT);

        entry.setExpandHorizontal(false);
        entry.setAlignHorizontal(Align.START);
        entry.setAlignVertical(Align.START);

        grid.attach(entry, 0, 1, 1, 1);

        label = entry;
    }

    /**
     * Put a label that is a "line above" (references; definition lists if we
     * ever do them). These are left aligned.
     */
    void setupLabelTop(final ListitemEntry widget) {
        final Label spacer;

        widget.setAlignVertical(Align.START);
        grid.attach(widget, 0, 0, 2, 1);

        spacer = new Label();
        spacer.setSizeRequest(WIDTH, -1);
        grid.attach(spacer, 0, 1, 1, 1);

        label = widget;
    }

    void setupBody(final EditorTextView view) {
        body = view;
        body.setExpandHorizontal(true);
        body.setExpandVertical(false);
        grid.attach(body, 1, 1, 1, 1);
    }

    public void advanceTo(Segment segment) {
        label.advanceTo(segment);
        body.advanceTo(segment);
    }

    public Entry getLabel() {
        return label;
    }

    public EditorTextView getTextView() {
        return body;
    }

    public void reverseTo(Segment segment) {
        label.reverseTo(segment);
        body.reverseTo(segment);
    }

    public void grabFocus() {
        label.selectRegion(0, 0);
        label.setPosition(-1);
        body.grabFocus();
    }
}
