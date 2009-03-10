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

import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Adjustment;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.HeadingSegment;
import quill.textbase.ParagraphSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;

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

    private VBox series;

    ComponentEditorWidget() {
        super();
        scroll = this;
        setupScrolling();
        hookupAdjustmentReactions();
    }

    private void setupScrolling() {
        series = new VBox(false, 3);

        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(series);

        adj = scroll.getVAdjustment();
    }

    private void hookupAdjustmentReactions() {
        scroll.connect(new ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                System.out.format("%4.0f + %3.0f = %4.0f to %4.0f\n", adj.getValue(), adj.getPageSize(),
                        adj.getValue() + adj.getPageSize(), adj.getUpper());
                return false;
            }
        });
    }

    void initializeSeries(Segment[] segments) {
        EditorTextView editor;
        HeadingBox heading;
        Widget widget;
        ScrolledWindow wide;

        for (Segment segment : segments) {
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
            } else {

                throw new IllegalStateException("Unknown Segment type");
            }

            series.packStart(widget, false, false, 0);
        }

        series.showAll();
    }
}
