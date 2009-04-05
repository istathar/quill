/*
 * RenderEngine.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package parchment.render;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.FontOptions;
import org.gnome.gtk.PaperSize;
import org.gnome.gtk.Unit;
import org.gnome.pango.Attribute;
import org.gnome.pango.AttributeList;
import org.gnome.pango.FontDescription;
import org.gnome.pango.FontDescriptionAttribute;
import org.gnome.pango.ForegroundColorAttribute;
import org.gnome.pango.Layout;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Rectangle;
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

/**
 * Render a Series.
 * 
 * <p>
 * This class and its concrete descendants work in in "points", which makes
 * sense since the ultimate target back end is PDF. When used to generate a
 * preview for screen, it's up to the rendering Widget to scale the Context to
 * the allocated size.
 * 
 * @author Andrew Cowie
 */
public abstract class RenderEngine
{
    private double pageWidth;

    private double pageHeight;

    private double topMargin;

    private double bottomMargin;

    private double leftMargin;

    private double rightMargin;

    private double cursor;

    private Series series;

    /*
     * Cached calculations of normal text metrics, used for ensuring uniform
     * line spacing
     */

    private double defaultLineHeight;

    private double defaultLineAscent;

    /**
     * Construct a new RenderEngine. Call {@link #render(Context) render()} to
     * actually draw.
     */
    protected RenderEngine(final PaperSize paper, final Series series) {
        loadFonts();
        processSize(paper);
        this.series = series;
    }

    /**
     * Given a Context, have the rendering engine to draw to it. This assumes
     * that the target Surface either a) has the size as the PaperSize passed
     * to the constructor, or b) has been scaled to that size.
     */
    public void render(final Context cr) {
        calculateDefaultSpacing(cr);
        processText(cr, series);
    }

    private void loadFonts() {
        fonts.serif = new FontDescription("Charis SIL, 8.0");
        fonts.mono = new FontDescription("Liberation Mono, 7.0");
        fonts.sans = new FontDescription("Liberation Sans, 7.0");
    }

    private void processSize(final PaperSize paper) {
        pageWidth = paper.getWidth(Unit.POINTS);
        pageHeight = paper.getHeight(Unit.POINTS);

        topMargin = 25;
        bottomMargin = 25;
        leftMargin = 45;
        rightMargin = 20;
    }

    private void calculateDefaultSpacing(final Context cr) {
        final Layout layout;
        final FontOptions options;
        final LayoutLine line;
        final Rectangle logical;

        layout = new Layout(cr);
        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(fonts.serif);

        layout.setText("Spacing");

        line = layout.getLineReadonly(0);
        logical = line.getExtentsLogical();
        defaultLineHeight = logical.getHeight();
        defaultLineAscent = logical.getAscent();
    }

    public void processText(final Context cr, final Series series) {
        int i;
        Segment segment;
        Text text;
        String str;
        Extract[] paras;

        cursor = topMargin;

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
                drawBlankLine(cr);
            } else if (segment instanceof ParagraphSegment) {
                paras = text.extractLines();
                for (Extract extract : paras) {
                    drawBlockText(cr, extract);
                    drawBlankLine(cr);
                }
            }
        }
    }

    private static final boolean debug = true;

    protected void drawBlankLine(Context cr) {
        cursor += defaultLineHeight;

        if (debug) {
            cr.setSource(1.0, 0.0, 0.1);
            cr.setLineWidth(0.1);
            cr.moveTo(leftMargin, cursor);
            cr.lineTo(pageWidth, cursor);
            cr.stroke();
            cr.setSource(0.0, 0.0, 0.0);
        }
    }

    protected void drawHeading(Context cr, String title, double size) {
        final Layout layout;
        final FontDescription desc;
        final FontOptions options;
        final LayoutLine line;
        final Rectangle ink;
        double b, v;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        desc = new FontDescription("Essays1743");
        desc.setSize(size);
        layout.setFontDescription(desc);

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText(title);

        cr.setSource(0.0, 0.0, 0.0);

        line = layout.getLineReadonly(0);
        ink = line.getExtentsInk();
        b = ink.getAscent();
        v = ink.getHeight();

        cr.moveTo(leftMargin, cursor + b);
        cr.showLayout(line);

        cursor += v;
    }

    protected void drawBlockText(Context cr, Extract extract) {
        final Layout layout;
        final FontOptions options;
        final StringBuilder buf;
        final double spacing;
        boolean first;
        double b, v = 0;
        AttributeList list;
        int i, j, len, offset, width;
        Span span;
        String str;
        Rectangle logical;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(fonts.serif);
        spacing = 0.0;

        layout.setWidth(pageWidth - (leftMargin + rightMargin));

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
                for (Attribute attr : attributesForMarkup(span.getMarkup())) {
                    if (attr != null) {
                        attr.setIndices(layout, offset, width);
                        list.insert(attr);
                    }
                }
            } else if (span instanceof StringSpan) {
                for (Attribute attr : attributesForMarkup(span.getMarkup())) {
                    if (attr != null) {
                        attr.setIndices(layout, offset, width);
                        list.insert(attr);
                    }
                }
            }

            offset += width;
        }

        layout.setAttributes(list);

        /*
         * Finally, we can render the individual lines of the paragraph.
         */

        cr.setSource(0.0, 0.0, 0.0);
        first = true;

        for (LayoutLine line : layout.getLinesReadonly()) {
            logical = line.getExtentsLogical();

            v = logical.getHeight();
            b = logical.getAscent();

            if (cursor + v > (pageHeight - bottomMargin)) {
                return;
            }

            if (!first) {
                cursor += spacing;
            }

            cr.moveTo(leftMargin, cursor + b);
            cr.showLayout(line);

            cursor += v;

            if (debug) {
                cr.setSource(0.1, 1.0, 0.1);
                cr.setLineWidth(0.1);
                cr.moveTo(leftMargin, cursor);
                cr.lineTo(pageWidth, cursor);
                cr.stroke();
                cr.setSource(0.0, 0.0, 0.0);
            }

            first = false;
        }
    }

    private static final Attribute[] empty = new Attribute[] {};

    /*
     * This is just a placeholder... move to rendering engine once we have
     * such things
     */
    private static Attribute[] attributesForMarkup(Markup m) {
        if (m == null) {
            return empty;
        }
        if (m instanceof Common) {
            if (m == Common.ITALICS) {
                return new Attribute[] {
                    new StyleAttribute(Style.ITALIC),
                };
            } else if (m == Common.BOLD) {
                return new Attribute[] {
                    new WeightAttribute(Weight.BOLD),
                };
            } else if (m == Common.FILENAME) {
                return new Attribute[] {
                        new FontDescriptionAttribute(fonts.mono), new StyleAttribute(Style.ITALIC),
                };
            } else if (m == Common.TYPE) {
                return new Attribute[] {
                    new FontDescriptionAttribute(fonts.sans),
                };
            } else if (m == Common.FUNCTION) {
                return new Attribute[] {
                    new FontDescriptionAttribute(fonts.mono),
                };
            } else if (m == Common.CODE) {
                return new Attribute[] {
                    new FontDescriptionAttribute(fonts.mono),
                };
            } else if (m == Common.APPLICATION) {
                return new Attribute[] {
                    new WeightAttribute(Weight.BOLD),
                };
            } else if (m == Common.COMMAND) {
                return new Attribute[] {
                        new FontDescriptionAttribute(fonts.mono),
                        new WeightAttribute(Weight.SEMIBOLD),
                        new ForegroundColorAttribute(0.1, 0.1, 0.1),
                };
            }

        } else if (m instanceof Preformat) {
            if (m == Preformat.USERINPUT) {
                return empty;
            }
        }
        // else TODO

        throw new IllegalArgumentException("\n" + "Translation of " + m + " not yet implemented");
    }

    protected void drawBlockProgram(Context cr, String prog) {
        final Layout layout;
        final FontOptions options;
        final String[] paras;
        double y, b, v = 0;
        Rectangle logical;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(fonts.mono);

        /*
         * This is fake. TODO pass in our TextStack object (which already
         * knows what the paragraphs are) and process from that.
         */
        paras = prog.split("\n");

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText("Workaround");

        cr.setSource(0.0, 0.0, 0.0);

        for (String para : paras) {
            layout.setText(para);

            for (LayoutLine line : layout.getLinesReadonly()) {
                logical = line.getExtentsLogical();
                v = logical.getHeight();
                b = logical.getAscent();

                y = cursor + v;

                if (y > (pageHeight - bottomMargin)) {
                    return;
                }

                cr.moveTo(leftMargin, cursor + b);
                cr.showLayout(line);

                cursor += v;
            }
        }
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

    /*
     * A series of getters for the calculated page dimension properties.
     * FUTURE should this become embedded in a wrapper object?
     */
    public double getPageWidth() {
        return pageWidth;
    }

    public double getPageHeight() {
        return pageHeight;
    }

    public double getMarginTop() {
        return topMargin;
    }

    public double getMarginLeft() {
        return leftMargin;
    }

    public double getMarginRight() {
        return rightMargin;
    }

    public double getMarginBottom() {
        return bottomMargin;
    }
}

class fonts
{
    static FontDescription sans;

    static FontDescription serif;

    static FontDescription mono;
}
