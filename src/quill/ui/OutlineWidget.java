/*
 * OutlineWidget.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Button;
import org.gnome.gtk.VBox;

import quill.textbase.ComponentSegment;
import quill.textbase.HeadingSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Text;

import static quill.client.Quill.data;

/**
 * A table of contents style interface to navigate the document. This could be
 * a lot fancier, but the tree of buttons conveys the idea.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO This is just a demonstration placeholder. It will need to be made live
 * and reactive to changes to the DataLayer.
 */
class OutlineWidget extends VBox
{
    private VBox top;

    public OutlineWidget() {
        super(false, 0);
        Series series;
        Segment segment;
        int i;
        Text text;
        StringBuilder str;
        Button button;

        top = this;
        series = data.getActiveDocument().get(0);

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);

            if (segment instanceof ComponentSegment) {
                text = segment.getText();

                str = new StringBuilder();
                str.append("<big>");
                str.append(text.toString());
                str.append("</big>");
                button = new Button(str.toString());
                top.packStart(button);
            } else if (segment instanceof HeadingSegment) {
                text = segment.getText();

                button = new Button(text.toString());
                top.packStart(button);
            }
        }
    }
}
