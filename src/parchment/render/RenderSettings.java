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
package parchment.render;

import org.gnome.gtk.PaperSize;
import org.gnome.pango.FontDescription;

import parchment.format.Stylesheet;
import parchment.format.UnsupportedValueException;

/**
 * Convert all the values from loaded from a Stylesheet into strong types for
 * use in a RenderEngine. In the process, this acts as a validator for the
 * <code>&lt;presentation&gt;</code> block of a <code>.parchment</code> file.
 * 
 * @author Andrew Cowie
 */
// immutable, not that it really matters.
class RenderSettings
{

    private final PaperSize paper;

    private final double marginTop;

    private final double marginLeft;

    private final double marginRight;

    private final double marginBottom;

    private final FontDescription fontSerif;

    private final FontDescription fontSans;

    private final FontDescription fontMono;

    private final FontDescription fontHeading;

    /**
     * Given a Stylesheet state, parse and process it into Java objects.
     */
    RenderSettings(final Stylesheet style) throws UnsupportedValueException {
        final String size, serif, sans, mono, heading;
        final String top, left, right, bottom;

        size = style.getPaperSize();
        this.paper = loadPaperType(size);

        top = style.getMarginTop();
        this.marginTop = loadMargin(top);

        left = style.getMarginLeft();
        this.marginLeft = loadMargin(left);

        right = style.getMarginRight();
        this.marginRight = loadMargin(right);

        bottom = style.getMarginBottom();
        this.marginBottom = loadMargin(bottom);

        serif = style.getFontSerif();
        this.fontSerif = loadDescription(serif);

        sans = style.getFontSans();
        this.fontSans = loadDescription(sans);

        mono = style.getFontMono();
        this.fontMono = loadDescription(mono);

        heading = style.getFontHeading();
        this.fontHeading = loadDescription(heading);
    }

    private static PaperSize loadPaperType(String size) throws UnsupportedValueException {
        if (size.equals("A4")) {
            return PaperSize.A4;
        } else if (size.equals("Letter")) {
            return PaperSize.LETTER;
        } else {
            /*
             * We don't support arbitrary paper types yet, sorry.
             */
            throw new UnsupportedValueException("Requested <paper size=\"" + size + "\"> invalid");
        }
    }

    /**
     * The .parchment files store measurements in mm; Cairo works in points.
     * Convert here. Note we never ask the user for margins in points, only in
     * metric.
     */
    private static double loadMargin(String milimetres) {
        final double mm, points;

        mm = Double.valueOf(milimetres);

        /*
         * 1 inch : 25.4 mm 72 points : 1 inch
         */

        points = mm / 25.4 * 72.0 / 1.0;

        return points;
    }

    /**
     * FUTURE It'd be nice if we could do real validation here, as in
     * "can you actually get the font you are requesting?" because if it falls
     * back due to a missing font that's actually a pretty big (output
     * visible) problem. Just checking the FontDescription's family isn't
     * really sufficient; that's just initial parsing. The reqal question is
     * how to access the actual font as chosen by fontconfig?
     */
    private static FontDescription loadDescription(String description) {
        return new FontDescription(description);
    }

    PaperSize getPaper() {
        return this.paper;
    }

    double getMarginTop() {
        return this.marginTop;
    }

    double getMarginLeft() {
        return this.marginLeft;
    }

    double getMarginRight() {
        return this.marginRight;
    }

    double getMarginBottom() {
        return this.marginBottom;
    }

    FontDescription getFontSerif() {
        return this.fontSerif;
    }

    FontDescription getFontSans() {
        return this.fontSans;
    }

    FontDescription getFontMono() {
        return this.fontMono;
    }

    FontDescription getFontHeading() {
        return this.fontHeading;
    }
}
