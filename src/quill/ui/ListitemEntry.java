/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2011 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.Editable;
import org.gnome.gtk.Entry;

import quill.textbase.Extract;
import quill.textbase.Segment;

/**
 * Editor for the "labels" of list items.
 * 
 * @author Andrew Cowie
 */
abstract class ListitemEntry extends Entry implements Editor
{
    private SeriesEditorWidget parent;

    private Segment segment;

    private Entry entry;

    public ListitemEntry(SeriesEditorWidget parent, Segment segment) {
        super();

        this.parent = parent;
        this.entry = this;

        entry.setHasFrame(false);

        apply(segment);

        entry.connect(new Entry.Changed() {
            public void onChanged(Editable source) {
                final Entry entry;
                final String value;

                entry = (Entry) source;
                value = entry.getText();

                handleLabelChanged(value);
            }
        });
    }

    private void handleLabelChanged(String value) {
        final Extract entire;
        final Segment previous, segment;

        previous = this.segment;
        segment = previous.createSimilar(value);

        parent.propegateTextualChange(this, previous, segment);
    }

    public Segment getSegment() {
        return segment;
    }

    public void advanceTo(Segment segment) {
        apply(segment);
    }

    public void reverseTo(Segment segment) {
        apply(segment);
    }

    public Entry getLabel() {
        return this;
    }

    public EditorTextView getTextView() {
        return null;
    }

    private void apply(Segment segment) {
        final String text;

        if (segment == this.segment) {
            return;
        }

        text = segment.getExtra();
        entry.setText(text);

        this.segment = segment;
    }
}
