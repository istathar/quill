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
import org.freedesktop.cairo.Surface;
import org.freedesktop.cairo.XlibSurface;
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

    private double extraSpacing;

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
     * Specify additional spacing to be added to the default line height. If
     * you've got a font whose extents are unreasonably spacious, then you can
     * use a negative value to pull it back (but, beware that if you specify a
     * negative delta that is greater than the ascent value, Bad Things will
     * happen).
     */
    protected void setSpacing(double spacing) {
        this.extraSpacing = spacing;
    }

    /**
     * Given a Context, have the rendering engine to draw to it. This assumes
     * that the target Surface either a) has the size as the PaperSize passed
     * to the constructor, or b) has been scaled to that size.
     */
    public void render(final Context cr) {
        /*
         * An instance of this class can only do one render at a time.
         */
        synchronized (this) {
            calculateDefaultSpacing(cr);
            processText(cr, series);
        }
    }

    /*
     * This will move to the actual RenderEngine subclass, I expect.
     */
    private void loadFonts() {
        fonts.serif = new FontDescription("Charis SIL, 8.0");
        setSpacing(-4.5);

        fonts.mono = new FontDescription("Inconsolata, 8.3");

        fonts.sans = new FontDescription("Liberation Sans, 7.3");

        fonts.heading = new FontDescription("Linux Libertine C");
    }

    private void processSize(final PaperSize paper) {
        pageWidth = paper.getWidth(Unit.POINTS);
        pageHeight = paper.getHeight(Unit.POINTS);

        topMargin = 25;
        bottomMargin = 25;
        leftMargin = 45;
        rightMargin = 30;
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

        layout.setText("Some text");

        line = layout.getLineReadonly(0);
        logical = line.getExtentsLogical();
        defaultLineHeight = logical.getHeight() + extraSpacing;
        defaultLineAscent = logical.getAscent() + extraSpacing;

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
                drawBlankLine(cr);
            } else if (segment instanceof HeadingSegment) {
                drawHeading(cr, str, 16.0);
                drawBlankLine(cr);
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

    private static final boolean debug = false;

    protected void drawBlankLine(Context cr) {
        cursor += defaultLineHeight * 0.7;

        if (debug) {
            cr.setSource(0.3, 0.0, 0.1);
            cr.setLineWidth(0.1);
            cr.moveTo(leftMargin, cursor);
            cr.lineTo(pageWidth - rightMargin, cursor);
            cr.stroke();
            cr.setSource(0.0, 0.0, 0.0);
        }
    }

    protected void drawHeading(Context cr, String title, double size) {
        final Layout layout;
        final FontDescription desc;
        final FontOptions options;
        LayoutLine line;
        final Rectangle ink;
        double b, d, v;
        int i, num;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        desc = fonts.heading.copy();
        desc.setSize(size);
        layout.setFontDescription(desc);

        layout.setWidth(pageWidth - (leftMargin + rightMargin));
        layout.setText(title);

        cr.setSource(0.0, 0.0, 0.0);

        /*
         * We draw headings at the from the ink extent, not the logical one;
         * otherwise the apparent top padding (especially from the top margin
         * if a heading is first on a page) is both variable and too large.
         */

        line = layout.getLineReadonly(0);
        ink = line.getExtentsInk();
        b = ink.getAscent();
        d = ink.getDescent();
        v = ink.getHeight();

        num = layout.getLineCount();

        cr.moveTo(leftMargin, cursor + b);
        cr.showLayout(line);

        cursor += v;

        for (i = 1; i < num; i++) {
            cr.moveTo(leftMargin, cursor + b);
            line = layout.getLineReadonly(i);
            cr.showLayout(line);
            cursor += v;
        }

        cursor += 3.0;
    }

    protected void drawBlockText(Context cr, Extract extract) {
        final Layout layout;
        final FontOptions options;
        final StringBuilder buf;
        AttributeList list;
        int i, j, len, offset, width;
        Span span;
        String str;

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(fonts.serif);

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
         * Finally, we can render the individual lines of the paragraph. We do
         * NOT use each line's logical extents! We keep the line spacing
         * consistent; it's up to the RenderEngine [subclass] and font choices
         * therein to ensure that the various markup being drawn stays between
         * the lines.
         */

        cr.setSource(0.0, 0.0, 0.0);

        for (LayoutLine line : layout.getLinesReadonly()) {
            if (!paginate(cr, defaultLineHeight)) {
                return;
            }

            cr.moveTo(leftMargin, cursor + defaultLineAscent);
            cr.showLayout(line);

            cursor += defaultLineHeight;

            if (debug) {
                cr.setSource(0.1, 1.0, 0.1);
                cr.setLineWidth(0.1);
                cr.moveTo(leftMargin, cursor);
                cr.lineTo(pageWidth - rightMargin, cursor);
                cr.stroke();
                cr.setSource(0.0, 0.0, 0.0);
            }
        }
    }

    /**
     * Check and see if there is sufficient vertical space for the requested
     * height. If not, finish the page being drawn and star a fresh one if
     * possible. Return false if the vertical cursor can't be restarted (which
     * is the case when drawing to PreviewWidget).
     */
    private boolean paginate(Context cr, double requestedHeight) {
        final Surface surface;

        if ((cursor + requestedHeight) > (pageHeight - bottomMargin)) {
            surface = cr.getTarget();
            if (surface instanceof XlibSurface) {
                return false;
            }

            surface.showPage();
            cursor = topMargin;
        }

        return true;
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

        cr.setSource(0.0, 0.0, 0.0);

        for (String para : paras) {
            layout.setText(para);

            for (LayoutLine line : layout.getLinesReadonly()) {
                logical = line.getExtentsLogical();
                v = logical.getHeight();
                b = logical.getAscent();

                if (!paginate(cr, v)) {
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

    static FontDescription heading;
}
