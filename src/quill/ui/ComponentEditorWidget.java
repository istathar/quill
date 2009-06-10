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
import quill.textbase.Extract;
import quill.textbase.HeadingSegment;
import quill.textbase.ParagraphSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.TextStack;

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
        Widget widget;

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);

            widget = editorForSegment(segment);

            box.packStart(widget, false, false, 0);
        }

        box.showAll();
    }

    private Widget editorForSegment(Segment segment) {
        EditorTextView editor;
        HeadingBox heading;
        ScrolledWindow wide;

        if (segment instanceof ParagraphSegment) {
            editor = new ParagraphEditorTextView();
            editor.loadText(segment.getText());

            return editor;
        } else if (segment instanceof PreformatSegment) {
            editor = new PreformatEditorTextView();
            editor.loadText(segment.getText());

            wide = new ScrolledWindow();
            wide.setPolicy(PolicyType.AUTOMATIC, PolicyType.NEVER);
            wide.add(editor);

            return wide;
        } else if (segment instanceof HeadingSegment) {
            heading = new SectionHeadingBox();
            heading.loadText(segment.getText());

            return heading;
        } else if (segment instanceof ComponentSegment) {
            heading = new ChapterHeadingBox();
            heading.loadText(segment.getText());

            return heading;
        } else {

            throw new IllegalStateException("Unknown Segment type");
        }
    }

    /**
     * Take view (an EditorTextView in this ComponentEditorWidget) and splice
     * it into two pieces, inserting a new Segment between.
     */
    void spliceSeries(EditorTextView view, int offset, Segment segment) {
        final Widget[] children;
        int i;
        final Extract extract;
        final TextStack stack;
        Widget widget;

        /*
         * Find the index of the view into the VBox.
         */

        children = box.getChildren();

        for (i = 0; i < children.length; i++) {
            if (children[i] == view) {
                break;
            }
        }
        if (i == children.length) {
            throw new IllegalArgumentException("view not in this ComponentEditorWidget");
        }

        /*
         * Create the new editor
         */

        widget = editorForSegment(segment);

        box.packStart(widget, false, false, 0);
        box.reorderChild(widget, i + 1);
        widget.showAll();

        /*
         * Split the old one in two pieces, adding a new editor for the second
         * piece.
         */

        extract = view.chopInTwo(offset);

        stack = new TextStack(extract);
        segment.setText(stack);

        widget = editorForSegment(segment);
    }
}
