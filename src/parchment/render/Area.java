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

/**
 * A rectangle of content which knows its height and which can be rendered to
 * a Page. These are lines of text, images, etc.
 * 
 * @author Andrew Cowie
 */
abstract class Area
{
    protected final double x;

    protected final double height;

    Area(final double x, final double height) {
        this.x = x;
        this.height = height;
    }

    /**
     * Get the height of this area, for flowing.
     */
    double getHeight() {
        return height;
    }

    /**
     * Draw this Area onto Context cr at top corner position y.
     */
    abstract void draw(Context cr, double y);
}

final class TextArea extends Area
{
    /**
     * Ascent.
     */
    /*
     * We could get this at draw() time from the LayoutLine, but we've already
     * calculated it in the RenderEngine, so just pass it in and we cache it
     * here.
     */
    private final double a;

    private final LayoutLine line;

    TextArea(final double x, final double height, final double ascent, final LayoutLine line) {
        this(x, height, ascent, line, false);
    }

    private final boolean error;

    public TextArea(double x, double height, double ascent, LayoutLine line, boolean error) {
        super(x, height);
        this.a = ascent;
        this.line = line;
        this.error = error;
    }

    void draw(final Context cr, final double y) {
        cr.moveTo(x, y + a);

        if (error) {
            cr.setSource(1.0, 0.0, 0.0);
        }

        cr.showLayout(line);

        if (error) {
            cr.setSource(0.0, 0.0, 0.0);
        }
    }
}

final class ImageArea extends Area
{
    private final Pixbuf pixbuf;

    private final double scale;

    /**
     * @param x
     *            offset from left margin.
     */
    ImageArea(final double x, final double height, final Pixbuf pixbuf, final double scale) {
        super(x, height);
        this.pixbuf = pixbuf;
        this.scale = scale;
    }

    void draw(final Context cr, final double y) {
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

final class BlankArea extends Area
{
    BlankArea(final double height) {
        super(0, height);
    }

    void draw(Context cr, double y) {}
}
