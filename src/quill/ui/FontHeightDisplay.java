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

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.FontOptions;
import org.freedesktop.cairo.Matrix;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Widget;
import org.gnome.pango.Attribute;
import org.gnome.pango.AttributeList;
import org.gnome.pango.FontDescription;
import org.gnome.pango.FontDescriptionAttribute;
import org.gnome.pango.Layout;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Rectangle;

import parchment.render.RenderEngine;
import parchment.render.RenderSettings;

import static org.freedesktop.cairo.HintMetrics.OFF;
import static org.freedesktop.cairo.HintStyle.NONE;

class FontHeightDisplay extends DrawingArea implements Widget.ExposeEvent
{
    private final DrawingArea drawing;

    /**
     * A cached RenderSettings object, which is derived from the current
     * Stylesheet. We use this to pull the fonts to be rendered.
     */
    private RenderSettings settings;

    FontHeightDisplay() {
        drawing = this;
        drawing.connect(this);
        drawing.setSizeRequest(-1, -1);
    }

    void setRenderer(RenderEngine engine) {
        this.settings = engine.getRenderSettings();
    }

    private double baseline, xheight, scaleFactor;

    public boolean onExposeEvent(Widget source, EventExpose event) {
        final Context cr;

        cr = new Context(event);
        layout = new Layout(cr);
        list = new AttributeList();

        setupText();
        calculateMetrics();
        shiftOutput(cr);
        drawLines(cr);

        scaleOutput(cr);
        drawText(cr);

        layout = null;
        list = null;
        return true;
    }

    private String text;

    private AttributeList list;

    private Layout layout;

    /**
     * Build up the string of text with Pango Attributes to be displayed. We
     * need this for rendering, of course, but also to scale the Widget.
     */
    private void setupText() {
        final StringBuffer buf;
        FontDescription desc;
        String str;
        int offset, width;
        Attribute attr;
        int i;

        buf = new StringBuffer();

        offset = 0;

        String[] texts;
        FontDescription[] fonts;

        texts = new String[] {
                "x Serif, ", "Sans, ", "Mono"
        };

        fonts = new FontDescription[] {
                settings.getFontSerif(), settings.getFontSans(), settings.getFontMono()
        };

        for (i = 0; i < texts.length; i++) {
            str = texts[i];

            buf.append(str);
            desc = fonts[i];
            attr = new FontDescriptionAttribute(desc);
            width = str.length();
            attr.setIndices(offset, width);
            list.insert(attr);
            offset += width;
        }

        text = buf.toString();
    }

    /**
     * Work out the vertical positions for the baseline and x-height lines.
     * This is done before scaling, and results in integral pixel values for
     * the two fields.
     */
    private void calculateMetrics() {
        final FontOptions options;
        final double pixelWidth, pixelHeight;
        final Allocation alloc;
        final FontDescription serif;
        LayoutLine line;
        Rectangle rect;
        final double scaleWidth, scaleHeight, fontWidth, fontHeight;
        final double ascent, height;

        /*
         * Turn off hinting, a) because we don't hint in RenderEngine, and b)
         * because pixel alignment is silly at such large scale. In any case,
         * the point is exactly consistent spacing and presentation across
         * scales. So, off.
         */

        options = new FontOptions();
        options.setHintMetrics(OFF);
        options.setHintStyle(NONE);
        layout.getContext().setFontOptions(options);

        /*
         * Work out the height of an 'x' for later use.
         */

        layout.setText("x");
        serif = settings.getFontSerif();
        layout.setFontDescription(serif);

        line = layout.getLineReadonly(0);
        rect = line.getExtentsInk();
        height = rect.getAscent();

        /*
         * Now work out the scaling factor. We want something that is wider
         * than the actual rendered text, and yet stable. Start with the text
         * String in all serif as an assumption.
         */

        alloc = super.getAllocation();
        pixelWidth = alloc.getWidth();
        pixelHeight = alloc.getHeight();

        layout.setText(text);

        line = layout.getLineReadonly(0);
        rect = line.getExtentsLogical();

        fontWidth = rect.getWidth();
        fontHeight = rect.getHeight();
        scaleWidth = pixelWidth / fontWidth;
        scaleHeight = pixelHeight / fontHeight;

        if (scaleWidth > scaleHeight) {
            scaleFactor = scaleHeight;
        } else {
            scaleFactor = scaleWidth;
        }

        /*
         * Now calculate the baseline and the x-height positions.
         */

        ascent = rect.getAscent();
        baseline = (int) (ascent * scaleFactor);
        xheight = (int) ((ascent - height) * scaleFactor);

        /*
         * Finally, set the actual text and attributes.
         */

        layout.setText(text);
        layout.setAttributes(list);
    }

    /**
     * Move the output down onto the integral pixel grid.
     */
    private void shiftOutput(final Context cr) {
        final Matrix matrix;

        matrix = new Matrix();
        matrix.translate(0.5, 0.5);
        cr.transform(matrix);
    }

    /**
     * Need to scale from pixels to points, which is what RenderEngines (and
     * more to the point, Cairo) works in internally.
     */
    private void scaleOutput(final Context cr) {
        final Matrix matrix;

        matrix = new Matrix();
        matrix.scale(scaleFactor, scaleFactor);
        cr.transform(matrix);
    }

    private void drawText(Context cr) {
        final LayoutLine line;

        line = layout.getLineReadonly(0);

        cr.setSource(0.0, 0.0, 0.0);
        cr.moveTo(1.0, baseline / scaleFactor);
        cr.showLayout(line);
    }

    private void drawLines(Context cr) {
        final Allocation alloc;
        final double width;

        alloc = super.getAllocation();
        width = alloc.getWidth();

        // red
        cr.setSource(1.0, 0.0, 0.0);
        cr.setLineWidth(1.0);

        cr.moveTo(0.0, baseline);
        cr.lineRelative(width, 0.0);
        cr.stroke();

        // blue
        cr.setSource(0.0, 0.0, 1.0);
        cr.moveTo(0.0, xheight);
        cr.lineRelative(width, 0.0);
        cr.stroke();
    }
}
