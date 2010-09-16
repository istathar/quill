/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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
import org.freedesktop.cairo.Matrix;
import org.gnome.gdk.EventExpose;
import org.gnome.gtk.Allocation;
import org.gnome.gtk.DrawingArea;
import org.gnome.gtk.Widget;

import parchment.format.Stylesheet;
import parchment.render.RenderEngine;
import quill.client.ApplicationException;
import quill.textbase.Folio;
import quill.textbase.Origin;

/**
 * Display a preview of what the final output document is going to be. This
 * shows the "current" page. Figuring out what the current page is requires a
 * fair bit of work; we start at the previous known hard page break (which is
 * likely a Chapter boundary or the start of the Article) and then render
 * forward from there until we reach the point we've been told to render to.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO We have to define what the tracking behaviour is. Follow the cursor?
 * Only when F2 is pressed? PgUp/PgDown with PreviewWidget focused?
 */
/*
 * FUTURE Also, one really nifty idea is that we could use the PreviewWidget
 * for navigation, ie, click on the area of a Segment as rendered and be taken
 * to the editor for that Segment! That will imply maintaining a table of
 * associations from rendered block to origin Segment, which will be a lot of
 * work especially in the face of a changing textbase. Nevertheless, since
 * this rerenders after a change, it should be mostly up to date, and we can
 * always force an invalidation on Segment creation/deletion.
 */
class PreviewWidget extends DrawingArea
{
    /*
     * Work in "points", which makes sense since the target back end is PDF.
     */

    private int pixelWidth;

    private int pixelHeight;

    private Folio folio;

    /**
     * What is the top level UI holding this document?
     */
    private PrimaryWindow primary;

    private RenderEngine engine;

    /**
     * The Stylesheet that we have configured; so long as this is unchanged we
     * can resuse the RenderEngine instance.
     */
    private Stylesheet style;

    PreviewWidget(PrimaryWindow window) {
        super();
        this.primary = window;

        this.connect(new Widget.ExposeEvent() {
            public boolean onExposeEvent(Widget source, EventExpose event) {
                final Context cr;
                final Origin cursor;

                cr = new Context(event);

                scaleOutput(cr, engine);
                drawPageOutline(cr, engine);
                drawCrosshairs(cr, engine);

                cursor = primary.getCursor();
                engine.render(cr, folio, cursor);

                return true;
            }
        });
    }

    private void drawPageOutline(Context cr, RenderEngine engine) {
        final double pageWidth, pageHeight;
        final double shadow = 3.0;

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();

        cr.rectangle(shadow, shadow, pageWidth, pageHeight);
        cr.setSource(0.1, 0.1, 0.1);
        cr.fill();

        cr.rectangle(0, 0, pageWidth, pageHeight);
        cr.setSource(1.0, 1.0, 1.0);
        cr.fillPreserve();
        cr.setSource(0.0, 0.0, 0.0);
        cr.setLineWidth(0.5);
        cr.stroke();
    }

    private void drawCrosshairs(Context cr, RenderEngine engine) {
        final double pageWidth, pageHeight;
        final double topMargin, bottomMargin, leftMargin, rightMargin;

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();
        topMargin = engine.getMarginTop();
        bottomMargin = engine.getMarginBottom();
        leftMargin = engine.getMarginLeft();
        rightMargin = engine.getMarginRight();

        drawCrosshairAt(cr, leftMargin, topMargin);
        drawCrosshairAt(cr, pageWidth - rightMargin, topMargin);
        drawCrosshairAt(cr, leftMargin, pageHeight - bottomMargin);
        drawCrosshairAt(cr, pageWidth - rightMargin, pageHeight - bottomMargin);
    }

    private void drawCrosshairAt(Context cr, final double x, final double y) {
        cr.setSource(0.8, 0.0, 0.8);

        cr.moveTo(x, y - 10);
        cr.lineRelative(0, 20);
        cr.moveTo(x - 10, y);
        cr.lineRelative(20, 0);
        cr.stroke();
    }

    private void scaleOutput(Context cr, RenderEngine engine) {
        final Allocation rect;
        final Matrix matrix;
        final double scaleWidth, scaleHeight, scaleFactor;
        final double pageWidth, pageHeight;

        rect = this.getAllocation();

        pixelWidth = rect.getWidth();
        pixelHeight = rect.getHeight();

        pageWidth = engine.getPageWidth();
        pageHeight = engine.getPageHeight();

        scaleWidth = pixelWidth / (pageWidth + 10.0);
        scaleHeight = pixelHeight / (pageHeight + 10.0);

        if (scaleWidth > scaleHeight) {
            scaleFactor = scaleHeight;
        } else {
            scaleFactor = scaleWidth;
        }

        matrix = new Matrix();
        matrix.scale(scaleFactor, scaleFactor);

        if (scaleWidth > scaleHeight) {
            matrix.translate(((pixelWidth / scaleFactor) - pageWidth) / 2.0, 0.0);
        } else {
            matrix.translate(0.0, ((pixelHeight / scaleFactor) - pageHeight) / 2.0);
        }

        /*
         * Bump the image off of the top left corner.
         */
        matrix.translate(0.5, 1.5);
        cr.transform(matrix);
    }

    /**
     * Given a Series in a Manuscript representing the Segments in a chapter
     * or article, instruct this Widget to render a preview of them.
     */
    void affect(Folio folio) {
        final Stylesheet style;

        this.folio = folio;

        /*
         * Now reconfigure the renderer if necessary.
         */

        style = folio.getStylesheet();
        if (this.style == style) {
            return;
        }

        try {
            engine = RenderEngine.createRenderer(style);
        } catch (ApplicationException rnfe) {
            // FIXME this has to be handled, but NOT here. Hm.
            throw new Error(rnfe);
        }
        this.style = style;
    }
}
