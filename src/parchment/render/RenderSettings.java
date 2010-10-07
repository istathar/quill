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
public class RenderSettings
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
        final String serif, sans, mono, heading;
        final String top, left, right, bottom;
        String size;

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
        size = style.getSizeSerif();
        this.fontSerif = loadDescription(serif, size);

        sans = style.getFontSans();
        size = style.getSizeSans();
        this.fontSans = loadDescription(sans, size);

        mono = style.getFontMono();
        size = style.getSizeMono();
        this.fontMono = loadDescription(mono, size);

        heading = style.getFontHeading();
        size = style.getSizeHeading();
        this.fontHeading = loadDescription(heading, size);
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
    static double convertMilimetresToPoints(double mm) {
        final double points;

        /*
         * 1 inch : 25.4 mm 72 points : 1 inch
         */

        points = mm / 25.4 * 72.0;

        return points;
    }

    /**
     * Take a size= value in milimetres, and convert it to points.
     */
    private static double loadMargin(String size) {
        final double mm, points;

        mm = Double.valueOf(size);
        points = convertMilimetresToPoints(mm);

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
    private static FontDescription loadDescription(String description, String size) {
        final FontDescription desc;
        final double mm, points;

        desc = new FontDescription(description);

        mm = Double.valueOf(size); // * 130.0 / 96.0 ?
        points = convertMilimetresToPoints(mm);
        desc.setSize(points);
        return desc;
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

    public FontDescription getFontSerif() {
        return this.fontSerif;
    }

    public FontDescription getFontSans() {
        return this.fontSans;
    }

    public FontDescription getFontMono() {
        return this.fontMono;
    }

    public FontDescription getFontHeading() {
        return this.fontHeading;
    }
}
