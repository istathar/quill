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

import org.gnome.gtk.PaperSize;
import org.gnome.pango.Style;
import org.gnome.pango.Weight;

import parchment.manuscript.RendererNotFoundException;
import parchment.manuscript.Stylesheet;
import parchment.manuscript.UnsupportedValueException;
import quill.ui.GraphicalTestCase;

import static parchment.render.RenderSettings.convertMilimetresToPoints;

/**
 * Check the validation logic when converting from Stylesheets to
 * RenderSettings.
 * 
 * @author Andrew Cowie
 */
public class ValidateStylesheetToRenderSettingsConversion extends GraphicalTestCase
{
    /*
     * The values here don't matter; they just have to match the ones in
     * Stylesheet so that our subsequent tests have known (here) values.
     */
    public final void testStylesheetDefault() {
        final Stylesheet style;

        style = new Stylesheet();

        assertEquals("parchment.render.ReportRenderEngine", style.getRendererClass());
        assertEquals("A4", style.getPaperSize());
        assertEquals("15.0", style.getMarginTop());
        assertEquals("20.0", style.getMarginLeft());
        assertEquals("12.5", style.getMarginRight());
        assertEquals("10.0", style.getMarginBottom());
        assertEquals("Linux Libertine O", style.getFontSerif());
        assertEquals("Liberation Sans", style.getFontSans());
        assertEquals("Inconsolata", style.getFontMono());
        assertEquals("Linux Libertine O C", style.getFontHeading());
        assertEquals("3.2", style.getSizeSerif());
        assertEquals("2.6", style.getSizeSans());
        assertEquals("3.0", style.getSizeMono());
        assertEquals("5.6", style.getSizeHeading());
    }

    public final void testRenderSettingsFromDefault() throws RendererNotFoundException,
            UnsupportedValueException {
        final Stylesheet style;
        final RenderSettings settings;

        style = new Stylesheet();
        settings = new RenderSettings(style);

        assertEquals(PaperSize.A4, settings.getPaper());

        assertEquals(42.52, settings.getMarginTop(), 0.01);
        assertEquals(56.69, settings.getMarginLeft(), 0.01);
        assertEquals(35.43, settings.getMarginRight(), 0.01);
        assertEquals(28.35, settings.getMarginBottom(), 0.01);

        assertEquals("Linux Libertine O", settings.getFontSerif().getFamily());
        assertEquals(convertMilimetresToPoints(3.2), settings.getFontSerif().getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.getFontSerif().getStyle());
        assertEquals(Weight.NORMAL, settings.getFontSerif().getWeight());

        assertEquals("Liberation Sans", settings.getFontSans().getFamily());
        assertEquals(convertMilimetresToPoints(2.6), settings.getFontSans().getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.getFontSans().getStyle());
        assertEquals(Weight.NORMAL, settings.getFontSans().getWeight());

        assertEquals("Inconsolata", settings.getFontMono().getFamily());
        assertEquals(convertMilimetresToPoints(3.0), settings.getFontMono().getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.getFontMono().getStyle());
        assertEquals(Weight.NORMAL, settings.getFontMono().getWeight());

        assertEquals("Linux Libertine O C", settings.getFontHeading().getFamily());
        assertEquals(convertMilimetresToPoints(5.6), settings.getFontHeading().getSize(), 0.01); // hm
        assertEquals(Style.NORMAL, settings.getFontHeading().getStyle());
        assertEquals(Weight.NORMAL, settings.getFontHeading().getWeight());
    }
}
