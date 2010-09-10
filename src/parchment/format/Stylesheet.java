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

    private final double marginTop;

    private final double marginLeft;

    private final double marginRight;

    private final double marginBottom;

    private final String fontSerif;

    private final String fontSans;

    private final String fontMono;

    private final String fontHeading;

    /**
     * Construct a blank ("default") Stylesheet.
     */
    /*
     * 2 cm = 56.67 pt
     */
    Stylesheet() {
        this.rendererClass = "parchment.render.RenderEngine";

        this.paperSize = "A4";

        this.marginTop = 40.0;
        this.marginLeft = 56.67;
        this.marginRight = 45.0;
        this.marginBottom = 30.0;

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

    public double getMarginTop() {
        return marginTop;
    }

    public double getMarginLeft() {
        return this.marginLeft;
    }

    public double getMarginRight() {
        return this.marginRight;
    }

    public double getMarginBottom() {
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
