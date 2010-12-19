/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.Matrix;
import org.freedesktop.cairo.MimeType;
import org.freedesktop.cairo.Pattern;
import org.freedesktop.cairo.Surface;
import org.gnome.gdk.Pixbuf;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Rectangle;

import quill.textbase.Origin;

/**
 * A rectangle of content which knows its height and which can be rendered to
 * a Page. These are lines of text, images, etc.
 * 
 * @author Andrew Cowie
 */
// immutable
abstract class Area
{
    protected final Origin origin;

    /**
     * The horizontal position of this Area, relative to the page edge.
     */
    protected final double x;

    protected final double height;

    Area(final Origin origin, final double x, final double height) {
        this.origin = origin;
        this.x = x;
        this.height = height;
    }

    /**
     * Get the height of this Area, for flowing.
     */
    double getHeight() {
        return height;
    }

    /**
     * Get the width of this Area. This is <i>very</i> preliminary and used
     * only for laying out labels at present.
     */
    double getWidth() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Where did this Area come from?
     */
    Origin getOrigin() {
        return origin;
    }

    /**
     * Draw this Area onto Context cr at top corner position y.
     */
    abstract void draw(Context cr, double y);

    /**
     * Create a CompositeArea which can be drawn as a single Area from the
     * supplied Areas.
     */
    static Area composite(Area[] areas) {
        int i;
        double height;
        Area area;

        height = 0;

        for (i = 0; i < areas.length; i++) {
            area = areas[i];
            height += area.height;
        }

        return new CompositeArea(height, areas);
    }
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

    private final boolean error;

    TextArea(final Origin origin, final double x, final double height, final double ascent,
            final LayoutLine line, final boolean error) {
        super(origin, x, height);
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

    double getWidth() {
        final Rectangle rect;
        final double width;

        rect = line.getExtentsLogical();
        width = rect.getWidth();

        return width;
    }
}

final class ImageArea extends Area
{
    private final String filename;

    private final double scale;

    /**
     * @param x
     *            offset from left margin.
     */
    ImageArea(final Origin origin, final double x, final double height, final String filename,
            final double scale) {
        super(origin, x, height);
        this.filename = filename;
        this.scale = scale;
    }

    /*
     * Modelled on java-gnome ValdiateCairoInternals.readFileIntoArray()
     */
    private static byte[] readFileIntoArray(String filename) throws IOException {
        final File source;
        final int length;
        final FileInputStream fis;
        final byte[] data;
        int actual;

        source = new File(filename);
        length = (int) source.length();

        data = new byte[length];

        fis = new FileInputStream(source);

        actual = 0;
        while (actual != length) {
            actual += fis.read(data, actual, length - actual);
        }

        return data;
    }

    void draw(final Context cr, final double y) {
        final Matrix matrix;
        final byte[] data;
        final Pixbuf pixbuf;
        final Pattern pattern;
        final Surface implicit;

        if (filename == null) {
            return;
        }

        try {
            cr.save();

            /*
             * This is a bit unusual; you'd think you would just moveTo(), or,
             * just as conventionally, use the x,y co-ordinates of
             * setSource(). But it works out just as cleanly to do a
             * translattion to (x,y) and then to just assume the source is
             * (0,0).
             */

            matrix = new Matrix();

            matrix.translate(x, y);
            matrix.scale(scale, scale);

            cr.transform(matrix);

            if (filename.endsWith(".jpg")) {
                data = readFileIntoArray(filename);
                pixbuf = new Pixbuf(data);
                cr.setSource(pixbuf, 0, 0);
                pattern = cr.getSource();
                implicit = pattern.getSurface();
                implicit.setMimeData(MimeType.JPEG, data);
            } else {
                pixbuf = new Pixbuf(filename);
                cr.setSource(pixbuf, 0, 0);
            }

            cr.paint();

            /*
             * Reset the source [colour] to black text.
             */

            cr.setSource(0.0, 0.0, 0.0);

        } catch (FileNotFoundException fnfe) {
            // should be impossible, we just tested for it
            throw new AssertionError();
        } catch (IOException ioe) {
            // bad news, but swollow
        } finally {
            cr.restore();
        }
    }
}

final class BlankArea extends Area
{
    BlankArea(final Origin origin, final double height) {
        super(origin, 0, height);
    }

    void draw(Context cr, double y) {
    // nothing :)
    }
}

final class PageBreakArea extends Area
{
    PageBreakArea(final Origin origin) {
        super(origin, 0, 0);
    }

    void draw(Context cr, double y) {
    // nothing :)
    }
}

/**
 * Special purpose Area for combining other Areas to force them aggregate
 * their heights before flowing. Create these with
 * {@link Area#composite(Area[])}.
 * 
 * @author Andrew Cowie
 */
final class CompositeArea extends Area
{
    private Area[] children;

    CompositeArea(final double height, Area[] areas) {
        super(areas[0].origin, 0.0, height);
        children = areas;
    }

    void draw(Context cr, double y) {
        int i;
        Area area;
        double d;

        d = y;

        for (i = 0; i < children.length; i++) {
            area = children[i];

            area.draw(cr, d);
            d += area.height;
        }
    }
}
