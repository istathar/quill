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

import org.gnome.gtk.Alignment;
import org.gnome.gtk.Button;
import org.gnome.gtk.Label;
import org.gnome.gtk.ReliefStyle;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.ComponentSegment;
import quill.textbase.HeadingSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Text;

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

    private Series series;

    public OutlineWidget() {
        super(false, 0);
        top = this;
        series = null;
    }

    private void buildOutline() {
        Segment segment;
        int i;
        Text text;
        StringBuilder str;
        Button button;
        Label label;

        if (series == null) {
            return;
        }

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);

            if (segment instanceof ComponentSegment) {
                text = segment.getText();

                str = new StringBuilder();
                str.append("<span size=\"xx-large\"> ");
                str.append(text.toString());
                str.append("</span>");
            } else if (segment instanceof HeadingSegment) {
                text = segment.getText();

                str = new StringBuilder();
                str.append("           ");
                str.append(text.toString());
            } else {
                continue;
            }

            button = new Button(str.toString());
            button.setRelief(ReliefStyle.NONE);
            label = (Label) button.getChild();
            label.setUseMarkup(true);
            label.setAlignment(Alignment.LEFT, Alignment.TOP);

            top.packStart(button, false, false, 0);
        }

        top.showAll();
    }

    /**
     * Given a Series, display it.
     */
    /*
     * At the moment this is horribly inefficient; we should have something
     * more dynamic that merely updates the Labels rather than wholesale
     * recreates everything.
     */
    void renderSeries(Series series) {
        this.series = series;

        for (Widget child : top.getChildren()) {
            top.remove(child);
        }

        buildOutline();
    }
}
