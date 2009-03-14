/*
 * ComponentEditorWidget.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Adjustment;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.ComponentSegment;
import quill.textbase.HeadingSegment;
import quill.textbase.ParagraphSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

/**
 * Left hand side of a PrimaryWindow for editing a Component (Article or
 * Chapter).
 * 
 * @author Andrew Cowie
 */
class ComponentEditorWidget extends ScrolledWindow
{
    private ScrolledWindow scroll;

    private Adjustment adj;

    private VBox box;

    ComponentEditorWidget() {
        super();
        scroll = this;
        setupScrolling();
        hookupAdjustmentReactions();
    }

    private void setupScrolling() {
        box = new VBox(false, 3);

        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(box);

        adj = scroll.getVAdjustment();
    }

    private void hookupAdjustmentReactions() {}

    /**
     * Tell the ComponentEditorWidget to ensure that the range from to
     * from+height is scrolled to and within view. This is used by the
     * EditorTextViews to handle the cursor moving one line above or below the
     * current viewport.
     */
    void ensureVisible(int from, int height) {
        int v, h;

        if (from < 0) {
            return;
        }

        v = (int) adj.getValue();
        h = (int) adj.getPageSize();

        if (from < v) {
            adj.setValue(from);
        } else if (from + height > v + h) {
            adj.setValue(from + height - h);
        }
    }

    void initializeSeries(Series series) {
        Segment segment;
        int i;
        EditorTextView editor;
        HeadingBox heading;
        Widget widget;
        ScrolledWindow wide;

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);

            if (segment instanceof ParagraphSegment) {
                editor = new ParagraphEditorTextView();
                editor.loadText(segment.getText());

                widget = editor;
            } else if (segment instanceof PreformatSegment) {
                editor = new PreformatEditorTextView();
                editor.loadText(segment.getText());

                wide = new ScrolledWindow();
                wide.setPolicy(PolicyType.AUTOMATIC, PolicyType.NEVER);
                wide.add(editor);

                widget = wide;
            } else if (segment instanceof HeadingSegment) {
                heading = new SectionHeadingBox();
                heading.loadText(segment.getText());

                widget = heading;
            } else if (segment instanceof ComponentSegment) {
                heading = new ChapterHeadingBox();
                heading.loadText(segment.getText());

                widget = heading;
            } else {

                throw new IllegalStateException("Unknown Segment type");
            }

            box.packStart(widget, false, false, 0);
        }

        box.showAll();
    }
}
