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

import org.freedesktop.cairo.Context;
import org.gnome.pango.Layout;
import org.gnome.pango.LayoutLine;
import org.gnome.pango.Rectangle;

/**
 * A RenderEngine for novels. This has no space between paragraphs, paragraphs
 * are indented, and each page has a header with the author and title...
 * 
 * @author Andrew Cowie
 */
/*
 * TODO work in progress
 */
public class NovelRenderEngine extends RenderEngine
{
    public NovelRenderEngine() {
        super();
    }

    protected void appendParagraphBreak(Context cr) {
    /*
     * Do nothing. In a novel, we indent paras, but don't have blank lines
     * between them.
     */
    }

    protected double getNormalIndent() {
        return 20.0;
    }

    /*
     * Code copied from overridden layoutAreaFooter()
     */
    protected Area[] layoutAreaFooter(Context cr, int pageNumber) {
        final Layout layout;
        final Rectangle ink;
        final LayoutLine line;
        final double pageWidth, footerHeight, rightMargin;
        Area area1, area2;

        layout = new Layout(cr);
        layout.setFontDescription(serifFace.desc);
        layout.setText(Integer.toString(pageNumber));
        ink = layout.getExtentsInk();

        pageWidth = super.getPageWidth();
        footerHeight = super.getFooterHeight();

        line = layout.getLineReadonly(0);
        area1 = new TextArea(null, (pageWidth - ink.getWidth()) / 2.0, footerHeight,
                serifFace.lineAscent, line, false);

        rightMargin = super.getMarginRight();
        area2 = new TextArea(null, pageWidth - rightMargin - ink.getWidth(), footerHeight,
                serifFace.lineAscent, line, false);

        return new Area[] {
                area1, area2
        };
    }
}
