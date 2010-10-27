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

    /*
     * Do nothing! In a novel, we indent paras, but don't have blank lines
     * between them.
     */
    protected void appendParagraphBreak(Context cr) {}

    /*
     * Set an indent on the first line of paragraphs.
     */
    protected double getNormalIndent() {
        return 20.0;
    }

    /*
     * Put the current page number centered at the bottom of the page.
     */
    protected Layout getFooterCenter(final Context cr, final int pageNumber) {
        final Layout result;
        final String text;

        result = new Layout(cr);
        result.setFontDescription(serifFace.desc);

        text = Integer.toString(pageNumber);
        result.setText(text);

        return result;
    }
}
