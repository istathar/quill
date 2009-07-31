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
import quill.textbase.TextChain;

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

    private double footerHeight;

    private double cursor;

    /**
     * Is there sufficient page area to keep drawing?
     */
    private boolean done;

    private int pageNumber;

    private Series series;

    Typeface sansFace;

    Typeface serifFace;

    Typeface monoFace;

    Typeface headingFace;

    /**
     * Construct a new RenderEngine. Call {@link #render(Context) render()} to
     * actually draw.
     */
    protected RenderEngine(final PaperSize paper, final Series series) {
        specifySize(paper);
        this.series = series;
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
            specifyFonts(cr);
            processSeries(cr, series);
        }
    }

    /*
     * This will move to the actual RenderEngine subclass, I expect.
     */
    private void specifyFonts(final Context cr) {
        serifFace = new Typeface(cr, new FontDescription("Charis SIL, 8.0"), -4.5);

        monoFace = new Typeface(cr, new FontDescription("Inconsolata, 8.3"), 0.0);

        sansFace = new Typeface(cr, new FontDescription("Liberation Sans, 7.3"), 0.0);

        headingFace = new Typeface(cr, new FontDescription("Linux Libertine C"), 0.0);
    }

    /*
     * 2 cm = 56.67 pt
     */
    private void specifySize(final PaperSize paper) {
        pageWidth = paper.getWidth(Unit.POINTS);
        pageHeight = paper.getHeight(Unit.POINTS);

        topMargin = 40.0;
        bottomMargin = 30.0;
        leftMargin = 56.67;
        rightMargin = 45.0;
    }

    public void processSeries(final Context cr, final Series series) {
        int i;
        Segment segment;
        TextChain text;
        Extract[] paras;

        cursor = topMargin;
        done = false;
        pageNumber = 0;
        footerHeight = serifFace.lineHeight;

        drawFooter(cr);
        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);
            text = segment.getText();

            if (segment instanceof ComponentSegment) {
                drawHeading(cr, text.extractAll(), 32.0);
                drawBlankLine(cr);
            } else if (segment instanceof HeadingSegment) {
                drawHeading(cr, text.extractAll(), 16.0);
                drawBlankLine(cr);
            } else if (segment instanceof PreformatSegment) {
                drawProgramCode(cr, text.extractAll());
                drawBlankLine(cr);
            } else if (segment instanceof ParagraphSegment) {
                paras = text.extractParagraphs();
                for (Extract extract : paras) {
                    drawNormalParagraph(cr, extract);
                    drawBlankLine(cr);
                }
            }
        }
    }

    protected void drawBlankLine(Context cr) {
        cursor += serifFace.lineHeight * 0.7;
    }

    protected void drawHeading(Context cr, Extract entire, double size) {
        FontDescription desc;
        Typeface face;

        desc = headingFace.desc.copy();
        desc.setSize(size);
        face = new Typeface(cr, desc, 0.0);

        drawAreaText(cr, entire, face, false);
    }

    protected void drawNormalParagraph(Context cr, Extract extract) {
        drawAreaText(cr, extract, serifFace, false);
    }

    protected void drawProgramCode(Context cr, Extract entire) {
        drawAreaText(cr, entire, monoFace, true);
    }

    private char previous;

    /**
     * Carry out smart typography replacements. Returns the number of
     * characters actually added, since some cases insert Unicode control
     * sequences.
     */
    private int translateAndAppend(final StringBuilder buf, final char ch, final boolean code) {
        int num, i;

        num = 0;

        if (code) {
            /*
             * Prevent Pango from doing line breaks on opening brackets.
             * U+2060 is the WORD JOINER character, similar to a zero width
             * space but with a more precise semantic.
             */

            if ((ch == '(') || (ch == '{') || (ch == '[')) {
                buf.append('\u2060');
                num++;
            }

            /*
             * Should we choose to replace spaces with non-breaking spaces in
             * code blocks, it's U+00A0. Anyway, now add the character.
             */

            buf.append(ch);
            num++;
        } else if (ch == '"') {
            /*
             * Replace normal quotes. When there's a space (or paragraph
             * start) preceeding the character we're considering, replace with
             * U+201C aka the LEFT DOUBLE QUOTATION MARK. Otherwise, close the
             * quotation with U+201D aka the RIGHT DOUBLE QUOTATION MARK.
             * Inspired by Smarty, of Markdown fame. Note that we don't do
             * this in preformatted code blocks
             */

            if (previous == '\0') {
                buf.append('“');
                num++;
            } else if (!Character.isWhitespace(previous)) {
                buf.append('”');
                num++;
            } else {
                buf.append('“');
                num++;
            }
        } else if (ch == ' ') {
            /*
             * If the preceeding sequence is " - " then replace the hyphen
             * with U+2014 EM DASH.
             */
            if (previous == '-') {
                i = buf.length();
                if ((i > 1) && (buf.charAt(i - 2) == ' ')) {
                    buf.setCharAt(i - 1, '\u2014');
                }
            }
            buf.append(' ');
            num++;
        } else {
            /*
             * Normal character. Just add it.
             */

            buf.append(ch);
            num++;
        }

        previous = ch;
        return num;
    }

    /**
     * Given an Extract of text and a FontDescription, render it to the target
     * Surface. Fancy typesetting character substitutions (smary quotes, etc)
     * will occur if not preformatted text.
     */
    protected final void drawAreaText(Context cr, Extract extract, Typeface face, boolean preformatted) {
        final Layout layout;
        final FontOptions options;
        final StringBuilder buf;
        final AttributeList list;
        int i, j, len, offset, width;
        boolean code;
        Span span;
        Markup format;
        String str;

        if (extract == null) {
            return;
        }

        if (done) {
            return;
        }

        layout = new Layout(cr);

        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(face.desc);

        layout.setWidth(pageWidth - (leftMargin + rightMargin));

        buf = new StringBuilder();

        list = new AttributeList();
        offset = 0;
        previous = '\0';

        /*
         * Now iterate over the Spans, accumulating their characters, and
         * creating Attributes along the way.
         */

        for (i = 0; i < extract.size(); i++) {
            span = extract.get(i);
            format = span.getMarkup();
            width = 0;

            if (preformatted) {
                code = true;
            } else if ((format == Common.LITERAL) || (format == Common.FILENAME)) {
                code = true;
            } else {
                code = false;
            }

            if (span instanceof CharacterSpan) {
                width += translateAndAppend(buf, span.getChar(), code);
            } else if (span instanceof StringSpan) {
                str = span.getText();
                len = str.length();

                for (j = 0; j < len; j++) {
                    width += translateAndAppend(buf, str.charAt(j), code);
                }
            }

            for (Attribute attr : attributesForMarkup(span.getMarkup())) {
                attr.setIndices(offset, width);
                list.insert(attr);
            }

            offset += width;
        }

        str = buf.toString();
        layout.setText(str);
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
            paginate(cr, face.lineHeight);
            if (done) {
                return;
            }

            cr.moveTo(leftMargin, cursor + face.lineAscent);
            cr.showLayout(line);

            cursor += face.lineHeight;
        }
    }

    /**
     * Check and see if there is sufficient vertical space for the requested
     * height. If not, finish the page being drawn and star a fresh one if
     * possible. Return false if the vertical cursor can't be restarted (which
     * is the case when drawing to PreviewWidget).
     */
    private void paginate(Context cr, double requestedHeight) {
        final Surface surface;

        if ((cursor + requestedHeight) > (pageHeight - bottomMargin - footerHeight)) {
            surface = cr.getTarget();
            if (surface instanceof XlibSurface) {
                done = true;
                return;
            }

            surface.showPage();
            cursor = topMargin;

            drawFooter(cr);
        }
    }

    private static final Attribute[] empty = new Attribute[] {};

    /*
     * This is just a placeholder... move to rendering engine once we have
     * such things
     */
    private Attribute[] attributesForMarkup(Markup m) {
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
                        new FontDescriptionAttribute(monoFace.desc), new StyleAttribute(Style.ITALIC),
                };
            } else if (m == Common.TYPE) {
                return new Attribute[] {
                    new FontDescriptionAttribute(sansFace.desc),
                };
            } else if (m == Common.FUNCTION) {
                return new Attribute[] {
                    new FontDescriptionAttribute(monoFace.desc),
                };
            } else if (m == Common.LITERAL) {
                return new Attribute[] {
                    new FontDescriptionAttribute(monoFace.desc),
                };
            } else if (m == Common.APPLICATION) {
                return new Attribute[] {
                    new WeightAttribute(Weight.BOLD),
                };
            } else if (m == Common.COMMAND) {
                return new Attribute[] {
                        new FontDescriptionAttribute(monoFace.desc),
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

    protected void drawFooter(Context cr) {
        final Layout layout;
        final Rectangle ink;

        pageNumber++;

        layout = new Layout(cr);
        layout.setFontDescription(serifFace.desc);
        layout.setText(Integer.toString(pageNumber));
        ink = layout.getExtentsInk();

        cr.setSource(0.0, 0.0, 0.0);
        cr.moveTo(pageWidth - rightMargin - ink.getWidth(), pageHeight - bottomMargin - footerHeight);
        cr.showLayout(layout);
    }
}
