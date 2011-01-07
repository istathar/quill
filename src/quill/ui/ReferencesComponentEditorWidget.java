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

import org.gnome.gtk.HBox;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextView;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WrapMode;

import quill.textbase.EndnoteSegment;
import quill.textbase.ReferenceSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

public class ReferencesComponentEditorWidget extends ComponentEditorWidget
{
    private SizeGroup group;

    ReferencesComponentEditorWidget(PrimaryWindow primary) {
        super(primary);
        setupLabels();
        addHeading("Endnotes");
        addHeading("References");
    }

    private void setupLabels() {
        group = new SizeGroup(HORIZONTAL);
    }

    private void addHeading(String title) {
        final Label heading;
        final VBox top;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLineWrap(true);
        heading.setLabel("<span size='xx-large'>" + title + "</span>");
        heading.setAlignment(LEFT, TOP);

        top = super.getTop();
        top.packStart(heading, false, false, 6);
    }

    protected Widget createEditorForSegment(int index, Segment segment) {
        final PrimaryWindow primary;

        primary = super.getPrimary();

        if (segment instanceof EndnoteSegment) {
            return createReferenceTagAndBody(segment);
        } else if (segment instanceof ReferenceSegment) {
            return createReferenceTagAndBody(segment);
        } else {
            // skip!
            return null;
        }
    }

    private Widget createReferenceTagAndBody(Segment segment) {
        final HBox hbox;
        final VBox vbox;
        final String str;
        final TextView ref, body;
        final TextBuffer one;

        hbox = new HBox(false, 0);

        str = segment.getExtra();

        one = new TextBuffer();
        one.setText(str);
        ref = new TextView(one);
        ref.setAcceptsTab(false);
        ref.setWrapMode(WrapMode.NONE);
        ref.setMarginLeft(10);
        ref.setJustify(Justification.RIGHT);
        vbox = new VBox(false, 0);
        vbox.packStart(ref, false, false, 0);
        hbox.packStart(vbox, false, false, 10);
        group.add(ref);

        body = new NormalEditorTextView(this, segment);

        hbox.packStart(body, true, true, 0);
        return hbox;
    }

    void initializeSeries(Series series) {
        final int I;
        final VBox top;
        int i;
        Segment segment;
        Widget widget;

        I = series.size();
        top = super.getTop();

        for (i = 0; i < I; i++) {
            segment = series.getSegment(i);
            widget = createEditorForSegment(i, segment);
            if (widget == null) {
                continue;
            }
            top.packStart(widget, false, false, 0);
        }

        top.showAll();
    }
}
