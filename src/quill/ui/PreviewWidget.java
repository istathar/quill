/*
 * PreviewWidget.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.FontOptions;
import org.freedesktop.cairo.Matrix;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.PaperSize;
import org.gnome.gtk.Unit;
import org.gnome.gtk.Widget;
import org.gnome.pango.Attribute;
import org.gnome.pango.AttributeList;
import org.gnome.pango.FontDescription;
import org.gnome.pango.FontDescriptionAttribute;
import org.gnome.pango.Layout;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Style;
import org.gnome.pango.StyleAttribute;
import org.gnome.pango.Weight;
import org.gnome.pango.WeightAttribute;

import quill.textbase.CharacterSpan;
import quill.textbase.Common;
import quill.textbase.ComponentSegment;
import quill.textbase.Extract;
import quill.textbase.HeadingSegment;
import quill.textbase.Markup;
import quill.textbase.ParagraphSegment;
import quill.textbase.Preformat;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.StringSpan;
import quill.textbase.Text;

import static org.freedesktop.cairo.HintMetrics.OFF;
import static quill.client.Quill.data;

/**
 * Display a preview of what the final output document is going to be. This
 * shows the "current" page. Figuring out what the current page is requires a
 * fair bit of work; we start at the previous known hard page break (which is
 * likely a Chapter boundary or the start of the Article) and then render
 * forward from there until we reach the point we've been told to render to.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO This is mostly a demonstrator at this point; we have yet to implement
 * the mechanism for communicating where the current editor point is. We also
 * have to define what the tracking behaviour is. Follow the cursor? Only when
 * F2 is pressed? PgUp/PgDown with PreviewWidget focused?
 * 
 * FUTURE Also, one really nifty idea is that we could use the PreviewWidget
 * for navigation, ie, click on the area of a Segment as rendered and be taken
 * to the editor for that Segment! That will imply maintaining a table of
 * associations from rendered block to origin Segment, which will be a lot of
 * work especially in the face of a changing textbase. Nevertheless, since
 * this rerenders after a change, it should be mostly up to date, and we can
 * always force an invalidation on Segment creation/deletion.
 */
class PreviewWidget extends DrawingArea
{
    /*
     * Work in "points", which makes sense since the target back end is PDF.
     */

    private int pixelWidth;

    private int pixelHeight;

    private double pageWidth;

    private double pageHeight;

    private double topMargin;

    private double bottomMargin;

    private double leftMargin;

    private double rightMargin;

    private double scaleFactor;

    private double cursor;

    PreviewWidget() {
        super();

        this.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;

                cr = new Context(source.getWindow());

                processSize(cr);
                scaleOutput(cr);
                drawPageOutline(cr);
                drawCrosshairs(cr);
                processText(cr);

                return true;
            }
        });
    }

    private void processSize(Context cr) {
        final Allocation rect;
        final PaperSize paper;

        rect = this.getAllocation();

        pixelWidth = rect.getWidth();
        pixelHeight = rect.getHeight();

        paper = PaperSize.A4;
        pageWidth = paper.getWidth(Unit.POINTS);
        pageHeight = paper.getHeight(Unit.POINTS);

        topMargin = 25;
        bottomMargin = 25;
        leftMargin = 45;
        rightMargin = 20;
    }

    private void drawPageOutline(Context cr) {
        final double shadow = 3.0;

        cr.rectangle(shadow, shadow, pageWidth, pageHeight);
        cr.setSource(0.1, 0.1, 0.1);
        cr.fill();

        cr.rectangle(0, 0, pageWidth, pageHeight);
        cr.setSource(1.0, 1.0, 1.0);
        cr.fillPreserve();
        cr.setSource(0.0, 0.0, 0.0);
        cr.setLineWidth(0.5);
        cr.stroke();
    }

    private void drawCrosshairs(Context cr) {
        drawCrosshairAt(cr, leftMargin, topMargin);
        drawCrosshairAt(cr, pageWidth - rightMargin, topMargin);
        drawCrosshairAt(cr, leftMargin, pageHeight - bottomMargin);
        drawCrosshairAt(cr, pageWidth - rightMargin, pageHeight - bottomMargin);
    }

    private void drawCrosshairAt(Context cr, final double x, final double y) {
        cr.setSource(0.8, 0.0, 0.8);

        cr.moveTo(x, y - 10);
        cr.lineRelative(0, 20);
        cr.moveTo(x - 10, y);
        cr.lineRelative(20, 0);
        cr.stroke();
    }

    public void processText(Context cr) {
        final Series series;
        int i;
        Segment segment;
        Text text;
        String str;
        Extract[] paras;

        cursor = topMargin;

        series = data.getActiveDocument().get(0);

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);
            text = segment.getText();

            /*
             * This is still mockup; the real implementation will work across
             * the Spans to aggregate consecutively formatted runs of text and
             * then apply Pango Attributes accordingly.
             */
            str = text.toString();

            if (segment instanceof ComponentSegment) {
                drawHeading(cr, str, 32.0);
            } else if (segment instanceof HeadingSegment) {
                drawHeading(cr, str, 16.0);
            } else if (segment instanceof PreformatSegment) {
                drawBlockProgram(cr, str);
            } else if (segment instanceof ParagraphSegment) {
                paras = text.extractLines();
                for (Extract extract : paras) {
                    drawBlockText(cr, extract);
                }
            }
        }
    }

    public void drawHeading(Context cr, String title, double size) {
        final Layout layout;
        final FontDescription desc;
        final FontOptions options;
        final LayoutLine line;
        double y, b, v;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        desc = new FontDescription("Liberation Serif");
        desc.setSize(size);
        layout.setFontDescription(desc);

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText(title);

        cr.setSource(0.0, 0.0, 0.0);

        line = layout.getLineReadonly(0);
        b = line.getExtentsInk().getAscent();
        v = line.getExtentsLogical().getHeight();
        y = cursor + b;

        line.getExtentsLogical().getAscent();
        cr.moveTo(leftMargin, y);
        cr.showLayout(line);

        cursor += v;
    }

    public void drawBlockText(Context cr, Extract extract) {
        final Layout layout;
        final FontDescription desc;
        final FontOptions options;
        final StringBuilder buf;
        double y, b, v = 0;
        boolean second;
        AttributeList list;
        Attribute attr;
        int i, j, len, offset, width;
        Span span;
        String str;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        desc = new FontDescription("Liberation Serif");
        desc.setSize(8.0);
        layout.setFontDescription(desc);

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText("Workaround");

        cr.setSource(0.0, 0.0, 0.0);

        b = layout.getBaseline();
        y = cursor + b;
        second = false;

        buf = new StringBuilder();

        for (i = 0; i < extract.size(); i++) {
            span = extract.get(i);

            if (span instanceof CharacterSpan) {
                buf.append(span.getChar());
            } else if (span instanceof StringSpan) {
                str = span.getText();
                len = str.length();
                for (j = 0; j < len; j++) {
                    buf.append(str.charAt(j));
                }
            }
        }

        str = buf.toString();
        layout.setText(str);

        /*
         * Now iterate over the Spans, and create Attributes for each
         * contiguous run.
         */

        list = new AttributeList();
        offset = 0;

        for (i = 0; i < extract.size(); i++) {
            span = extract.get(i);
            width = span.getWidth();

            if (span instanceof CharacterSpan) {
                attr = attributesForMarkup(span.getMarkup());
                if (attr != null) {
                    attr.setIndices(layout, offset, width);
                    list.insert(attr);
                }

            } else if (span instanceof StringSpan) {
                attr = attributesForMarkup(span.getMarkup());
                if (attr != null) {
                    attr.setIndices(layout, offset, width);
                    list.insert(attr);
                }
            }

            offset += width;
        }

        layout.setAttributes(list);

        // if (second) {
        // y += v; // blank line between paras
        // }

        /*
         * Finally, we can render the individual lines of the paragraph.
         */

        for (LayoutLine line : layout.getLinesReadonly()) {
            v = line.getExtentsLogical().getHeight();

            if (y > (pageHeight - bottomMargin)) {
                return;
            }
            cr.moveTo(leftMargin, y);
            cr.showLayout(line);

            y += v;
        }

        // second = true;

        cursor = y;
    }

    /*
     * This is just a placeholder... move to rendering engine once we have
     * such things
     */
    private static Attribute attributesForMarkup(Markup m) {
        if (m == null) {
            return null;
        }
        if (m instanceof Common) {
            if (m == Common.ITALICS) {
                return new StyleAttribute(Style.ITALIC);
            } else if (m == Common.BOLD) {
                return new WeightAttribute(Weight.BOLD);
            } else if (m == Common.FILENAME) {
                return new FontDescriptionAttribute(new FontDescription("Liberation Serif, Italic 8"));
            } else if (m == Common.TYPE) {
                return new FontDescriptionAttribute(new FontDescription("Liberation Sans, 8"));
            } else if (m == Common.FUNCTION) {
                return new FontDescriptionAttribute(new FontDescription("Liberation Mono, 8"));
            } else if (m == Common.APPLICATION) {
                return new WeightAttribute(Weight.BOLD);
            }
        } else if (m instanceof Preformat) {
            if (m == Preformat.USERINPUT) {
                return null;
            }
        }
        // else TODO

        throw new IllegalArgumentException("\n" + "Translation of " + m + " not yet implemented");
    }

    public void drawBlockProgram(Context cr, String prog) {
        final Layout layout;
        final FontDescription desc;
        final FontOptions options;
        final String[] paras;
        double y, b, v = 0;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        desc = new FontDescription("Liberation Mono");
        desc.setSize(8.0);
        layout.setFontDescription(desc);

        /*
         * This is fake. TODO pass in our TextStack object (which already
         * knows what the paragraphs are) and process from that.
         */
        paras = prog.split("\n");

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText("Workaround");

        cr.setSource(0.0, 0.0, 0.0);

        b = layout.getBaseline();
        y = cursor + b;
        for (String para : paras) {
            layout.setText(para);

            for (LayoutLine line : layout.getLinesReadonly()) {
                v = line.getExtentsLogical().getHeight();

                if (y > (pageHeight - bottomMargin)) {
                    return;
                }
                cr.moveTo(leftMargin, y);
                cr.showLayout(line);

                y += v;
            }

            // no blank line between paras
        }

        cursor = y;
    }

    private void scaleOutput(Context cr) {
        final Matrix matrix;
        final double scaleWidth, scaleHeight;

        scaleWidth = pixelWidth / (pageWidth + 10.0);
        scaleHeight = pixelHeight / (pageHeight + 10.0);

        if (scaleWidth > scaleHeight) {
            scaleFactor = scaleHeight;
        } else {
            scaleFactor = scaleWidth;
        }

        matrix = new Matrix();
        matrix.scale(scaleFactor, scaleFactor);

        if (scaleWidth > scaleHeight) {
            matrix.translate(((pixelWidth / scaleFactor) - pageWidth) / 2.0, 0.0);
        } else {
            matrix.translate(0.0, ((pixelHeight / scaleFactor) - pageHeight) / 2.0);
        }

        /*
         * Bump the image off of the top left corner.
         */
        matrix.translate(0.5, 1.5);
        cr.transform(matrix);
    }

    /**
     * Given a Series representing the Segments in a chapter or article,
     * instruct this Widget to render a preview of them.
     */
    /*
     * This will need refinement, obviously, once we start having live preview
     * and start dealing with multiple pages.
     */
    void renderSeries(Series series) {

    }
}
