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

import org.freedesktop.cairo.Antialias;
import org.freedesktop.cairo.Context;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Button;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Label;
import org.gnome.gtk.ReliefStyle;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;

import quill.textbase.ComponentSegment;
import quill.textbase.Extract;
import quill.textbase.HeadingSegment;
import quill.textbase.PreformatSegment;
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
        int i, num;
        Text text;
        StringBuilder str;
        Button button;
        Label label;
        DrawingArea lines;

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
                str.append("      ");
                str.append(text.toString());
            } else {
                text = segment.getText();
                text.extractParagraphs();
                if (segment instanceof PreformatSegment) {
                    lines = new CompressedLines(text, true);
                } else {
                    lines = new CompressedLines(text, false);
                }
                top.packStart(lines, false, false, 0);
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

class CompressedLines extends DrawingArea
{
    private int[] dots;

    private int num;

    private boolean pre;

    private static final int WIDTH = 200;

    private static final int SPACING = 3;

    CompressedLines(Text text, boolean program) {
        super();
        final DrawingArea self;
        Extract[] paras;
        String line;
        int j, i, words, width;

        self = this;

        paras = text.extractParagraphs();
        dots = new int[paras.length];
        j = 0;

        for (Extract extract : paras) {
            line = extract.getText();
            i = -1;
            words = 0;

            do {
                i++;
                words++;
            } while ((i = line.indexOf(' ', i)) != -1);

            dots[j++] = words;
        }

        num = 1;
        width = 0;
        for (i = 0; i < dots.length; i++) {
            width += dots[i] + 2;
            if (width > WIDTH) {
                num++;
                width = 0;
            }
        }

        pre = program;

        self.setSizeRequest(40 + WIDTH, SPACING * num);

        this.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;
                int i, j, width, rem;

                cr = new Context(source.getWindow());

                if (pre) {
                    cr.setSource(0.7, 0.7, 0.7);
                } else {
                    cr.setSource(0.5, 0.5, 0.5);
                }

                cr.setLineWidth(1.0);
                cr.setAntialias(Antialias.NONE);

                cr.moveTo(40, 0);
                width = 0;
                j = 0;

                for (i = 0; i < dots.length; i++) {
                    if (width + dots[i] > WIDTH) {
                        rem = WIDTH - width;
                        cr.lineRelative(rem, 0);
                        j++;
                        cr.moveTo(40, SPACING * j);
                        cr.lineRelative(dots[i] - rem, 0);
                        width = dots[i] - rem + 2;
                    } else {
                        cr.lineRelative(dots[i], 0);
                        width += dots[i] + 2;
                    }
                    cr.moveRelative(2, 0);
                }

                cr.stroke();

                return true;
            }
        });
    }
}
