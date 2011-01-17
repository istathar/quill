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

import org.gnome.gdk.Color;
import org.gnome.gdk.EventButton;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Entry;
import org.gnome.gtk.EventBox;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.StateType;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.Extract;
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

    private final VBox top;

    private final HBox box;

    /*
     * This is a terrible name, but what else are we supposed to use?
     */
    private ListitemEntry label;

    private EditorTextView body;

    private final SeriesEditorWidget parent;

    ListitemBox(final SeriesEditorWidget parent) {
        super();
        eb = this;

        top = new VBox(false, 0);
        box = new HBox(false, 0);
        top.packEnd(box, false, false, 0);
        eb.add(top);

        eb.modifyBackground(StateType.NORMAL, Color.WHITE);

        eb.connect(new Widget.ButtonPressEvent() {
            public boolean onButtonPressEvent(Widget source, EventButton event) {
                // TODO
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
        final Alignment align;

        entry.setSizeRequest(WIDTH - left, -1);
        entry.setAlignment(Alignment.LEFT);

        align = new Alignment(Alignment.LEFT, Alignment.TOP, 0.0f, 0.0f);
        align.setPadding(top, 0, left, 0);
        align.add(entry);

        box.packStart(align, false, false, 0);

        label = entry;
    }

    /**
     * Put a label that is a "line above" (references; definition lists if we
     * ever do them). These are left aligned.
     */
    void setupLabelTop(final ListitemEntry widget) {
        final Label spacer;

        spacer = new Label();
        spacer.setSizeRequest(WIDTH, -1);
        top.packStart(widget, false, false, 0);
        box.packStart(spacer, false, false, 0);

        label = widget;
    }

    private void handleLabelChanged(String value) {
        final Extract entire;
        final Segment previous, segment;

        previous = body.getSegment();
        segment = previous.createSimilar(value);

        parent.propegateTextualChange(body, previous, segment);
    }

    void setupBody(final EditorTextView view) {
        body = view;
        box.packStart(body, true, true, 0);
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

}
