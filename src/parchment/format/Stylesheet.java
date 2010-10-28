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

    private final String sizeSerif;

    private final String sizeSans;

    private final String sizeMono;

    private final String sizeHeading;

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

        this.fontSerif = "Linux Libertine";
        this.fontSans = "Liberation Sans";
        this.fontMono = "Inconsolata";
        this.fontHeading = "Linux Libertine O C";

        this.sizeSerif = "3.2";
        this.sizeSans = "2.6";
        this.sizeMono = "3.0";
        this.sizeHeading = "5.6";
    }

    /**
     * Build a new Stylesheet. Used when loading!
     */
    public Stylesheet(final String rendererClass, final String paperSize, final String marginTop,
            final String marginLeft, final String marginRight, final String marginBottom,
            final String fontSerif, final String fontSans, final String fontMono,
            final String fontHeading, final String sizeSerif, final String sizeSans,
            final String sizeMono, final String sizeHeading) {
        super();
        this.rendererClass = rendererClass;
        this.paperSize = paperSize;
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.fontSerif = fontSerif;
        this.fontSans = fontSans;
        this.fontMono = fontMono;
        this.fontHeading = fontHeading;
        this.sizeSerif = sizeSerif;
        this.sizeSans = sizeSans;
        this.sizeMono = sizeMono;
        this.sizeHeading = sizeHeading;
    }

    public String getRendererClass() {
        return rendererClass;
    }

    public Stylesheet changeRendererClass(String value) {
        return new Stylesheet(value, paperSize, marginTop, marginLeft, marginRight, marginBottom,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getPaperSize() {
        return this.paperSize;
    }

    public Stylesheet changePaperSize(String value) {
        return new Stylesheet(rendererClass, value, marginTop, marginLeft, marginRight, marginBottom,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getMarginTop() {
        return marginTop;
    }

    public Stylesheet changeMarginTop(String value) {
        return new Stylesheet(rendererClass, paperSize, value, marginLeft, marginRight, marginBottom,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getMarginLeft() {
        return this.marginLeft;
    }

    public Stylesheet changeMarginLeft(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, value, marginRight, marginBottom,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getMarginRight() {
        return this.marginRight;
    }

    public Stylesheet changeMarginRight(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, value, marginBottom,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getMarginBottom() {
        return this.marginBottom;
    }

    public Stylesheet changeMarginBottom(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight, value,
                fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono, sizeHeading);
    }

    public String getFontSerif() {
        return fontSerif;
    }

    public Stylesheet changeFontSerif(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, value, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono,
                sizeHeading);
    }

    public String getFontSans() {
        return fontSans;
    }

    public Stylesheet changeFontSans(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, value, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono,
                sizeHeading);
    }

    public String getFontMono() {
        return fontMono;
    }

    public Stylesheet changeFontMono(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, value, fontHeading, sizeSerif, sizeSans, sizeMono,
                sizeHeading);
    }

    public String getFontHeading() {
        return fontHeading;
    }

    public Stylesheet changeFontHeading(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, value, sizeSerif, sizeSans, sizeMono,
                sizeHeading);
    }

    public String getSizeSerif() {
        return sizeSerif;
    }

    public Stylesheet changeSizeSerif(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, fontHeading, value, sizeSans, sizeMono,
                sizeHeading);
    }

    public String getSizeSans() {
        return sizeSans;
    }

    public Stylesheet changeSizeSans(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, fontHeading, sizeSerif, value, sizeMono,
                sizeHeading);
    }

    public String getSizeMono() {
        return sizeMono;
    }

    public Stylesheet changeSizeMono(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, value,
                sizeHeading);
    }

    public String getSizeHeading() {
        return sizeHeading;
    }

    public Stylesheet changeSizeHeading(String value) {
        return new Stylesheet(rendererClass, paperSize, marginTop, marginLeft, marginRight,
                marginBottom, fontSerif, fontSans, fontMono, fontHeading, sizeSerif, sizeSans, sizeMono,
                value);
    }

}
