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
package parchment.format;

/**
 * Configuration for a RenderEngine. This information is persisted as the
 * <code>&lt;presentation&gt;</code> element in a <code>.parchment</code>
 * file.
 * 
 * <p>
 * This is intented to be subordinate to a Folio, as a part of a current
 * state.
 * 
 * <p>
 * Note that there are a <i>very</i> limited number of knobs that you can
 * twist here. Virtually everything about how a series of Segments is laid out
 * onto a page is controlled by a RenderEngine; if you need to alter some
 * behaviour you subclass that.
 * 
 * @author Andrew Cowie
 */
// immutable
public class Stylesheet
{
    private final String rendererClass;

    private final String paperSize;

    /**
     * Margins are in milimeters
     */
    private final String marginTop;

    private final String marginLeft;

    private final String marginRight;

    private final String marginBottom;

    /**
     * Font sizes are in "points", not that it really means anything, as
     * rendered height depends on the surface's DPI.
     */
    private final String fontSerif;

    private final String fontSans;

    private final String fontMono;

    private final String fontHeading;

    /**
     * Construct a blank ("default") Stylesheet.
     */
    public Stylesheet() {
        this.rendererClass = "parchment.render.ReportRenderEngine";

        this.paperSize = "A4";

        this.marginTop = "15.0";
        this.marginLeft = "20.0";
        this.marginRight = "12.5";
        this.marginBottom = "10.0";

        this.fontSerif = "Linux Libertine, 9.0";
        this.fontSans = "Liberation Sans, 8.0";
        this.fontMono = "Inconsolata, 8.1";
        this.fontHeading = "Linux Libertine O C";
    }

    public String getRendererClass() {
        return rendererClass;
    }

    public String getPaperSize() {
        return this.paperSize;
    }

    public String getMarginTop() {
        return marginTop;
    }

    public String getMarginLeft() {
        return this.marginLeft;
    }

    public String getMarginRight() {
        return this.marginRight;
    }

    public String getMarginBottom() {
        return this.marginBottom;
    }

    public String getFontSerif() {
        return fontSerif;
    }

    public String getFontSans() {
        return fontSans;
    }

    public String getFontMono() {
        return fontMono;
    }

    public String getFontHeading() {
        return fontHeading;
    }
}
