/*
 * Area.java
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
import org.freedesktop.cairo.Matrix;
import org.gnome.gdk.Pixbuf;
import org.gnome.pango.LayoutLine;

abstract class Area
{
    Area() {}

    abstract void draw(Context cr);
}

final class TextArea extends Area
{
    private final double x;

    private final double y;

    private final LayoutLine line;

    TextArea(final double x, final double y, final LayoutLine line) {
        this.x = x;
        this.y = y;
        this.line = line;
    }

    void draw(final Context cr) {
        cr.moveTo(x, y);
        cr.showLayout(line);
    }
}

final class ImageArea extends Area
{
    private final double x;

    private final double y;

    private final Pixbuf pixbuf;

    private final double scale;

    ImageArea(final double x, final double y, final Pixbuf pixbuf, final double scale) {
        this.x = x;
        this.y = y;
        this.pixbuf = pixbuf;
        this.scale = scale;
    }

    void draw(final Context cr) {
        final Matrix matrix;

        /*
         * This is a bit unusual; you'd think you would just moveTo(), or,
         * just as conventionally, use the x,y co-ordinates of setSource().
         * But it works out just as cleanly to do a translattion to (x,y) and
         * then to just assume the source is (0,0).
         */

        cr.save();
        matrix = new Matrix();

        matrix.translate(x, y);
        matrix.scale(scale, scale);

        cr.transform(matrix);
        cr.setSource(pixbuf, 0, 0);
        cr.paint();

        cr.restore();

        /*
         * Reset the source [colour] to black text.
         */

        cr.setSource(0.0, 0.0, 0.0);
    }
}
