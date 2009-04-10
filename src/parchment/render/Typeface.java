/*
 * Typeface.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package parchment.render;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.FontOptions;
import org.gnome.pango.FontDescription;
import org.gnome.pango.Layout;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Rectangle;

import static org.freedesktop.cairo.HintMetrics.OFF;

/**
 * Information about a font, including its description and metrics. Used for
 * layout purposes by RenderEngine.
 * 
 * @author Andrew Cowie
 */
/*
 * FUTURE It would be fabulous if we could cache the actual PangoFont lookup,
 * however that works. Surely looking up FontDescriptions every time is
 * costly.
 */
class Typeface
{
    final FontDescription desc;

    final double lineHeight;

    final double lineAscent;

    /**
     * @param extraSpacing
     *            Specify additional spacing to be added to the default line
     *            height. If you've got a font whose extents are unreasonably
     *            spacious, then you can use a negative value to pull it back
     *            (but, beware that if you specify a negative delta that is
     *            greater than the ascent value, Bad Things will happen).
     */
    Typeface(Context cr, FontDescription desc, double extraSpacing) {
        final Layout layout;
        final FontOptions options;
        final LayoutLine line;
        final Rectangle logical;

        this.desc = desc;

        layout = new Layout(cr);
        options = new FontOptions();
        options.setHintMetrics(OFF);
        layout.getContext().setFontOptions(options);

        layout.setFontDescription(desc);

        layout.setText("Some text");

        line = layout.getLineReadonly(0);
        logical = line.getExtentsLogical();

        this.lineHeight = logical.getHeight() + extraSpacing;
        this.lineAscent = logical.getAscent() + extraSpacing;
    }
}
