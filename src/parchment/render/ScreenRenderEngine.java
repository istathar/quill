/*
 * ScreenRenderEngine.java
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
import org.gnome.gtk.PaperSize;
import org.gnome.pango.FontDescription;

import quill.textbase.DataLayer;
import quill.textbase.Series;

/**
 * A render engine outputting to a page size with an aspect ratio actually
 * appropriate for reading on-screen in a PDF viewer, not printed to paper.
 * 
 * @author Andrew Cowie
 */
/*
 * Note that this isn't meant to be the screen preview of some other document
 * form; it's a stylesheet in its own right, with typefaces chosen
 * accordingly.
 */
public class ScreenRenderEngine extends RenderEngine
{
    public ScreenRenderEngine(PaperSize paper, DataLayer data, Series series) {
        super(paper, data, series);
    }

    protected void specifyFonts(final Context cr) {
        serifFace = new Typeface(cr, new FontDescription("Linux Libertine, 9.1"), 0.0);

        monoFace = new Typeface(cr, new FontDescription("Inconsolata, 8.3"), 0.0);

        sansFace = new Typeface(cr, new FontDescription("Liberation Sans, 7.3"), 0.0);

        headingFace = new Typeface(cr, new FontDescription("Linux Libertine O C"), 0.0);
    }
}
