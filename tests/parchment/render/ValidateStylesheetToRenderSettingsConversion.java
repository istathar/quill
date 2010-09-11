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

import parchment.format.RendererNotFoundException;
import parchment.format.Stylesheet;
import parchment.format.UnsupportedValueException;
import quill.ui.GraphicalTestCase;

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
        assertEquals("Linux Libertine, 9.0", style.getFontSerif());
        assertEquals("Liberation Sans, 8.0", style.getFontSans());
        assertEquals("Inconsolata, 8.1", style.getFontMono());
        assertEquals("Linux Libertine O C", style.getFontHeading());
    }

    public final void testRenderSettingsFromDefault() throws RendererNotFoundException,
            UnsupportedValueException {
        final Stylesheet style;
        final RenderSettings settings;

        style = new Stylesheet();
        settings = new RenderSettings(style);

        assertEquals(parchment.render.ReportRenderEngine.class, settings.type);
        assertEquals(PaperSize.A4, settings.paper);

        assertEquals(42.52, settings.marginTop, 0.01);
        assertEquals(56.69, settings.marginLeft, 0.01);
        assertEquals(35.43, settings.marginRight, 0.01);
        assertEquals(28.35, settings.marginBottom, 0.01);

        assertEquals("Linux Libertine", settings.fontSerif.getFamily());
        assertEquals(9.0, settings.fontSerif.getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.fontSerif.getStyle());
        assertEquals(Weight.NORMAL, settings.fontSerif.getWeight());

        assertEquals("Liberation Sans", settings.fontSans.getFamily());
        assertEquals(8.0, settings.fontSans.getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.fontSans.getStyle());
        assertEquals(Weight.NORMAL, settings.fontSans.getWeight());

        assertEquals("Inconsolata", settings.fontMono.getFamily());
        assertEquals(8.1, settings.fontMono.getSize(), 0.01);
        assertEquals(Style.NORMAL, settings.fontMono.getStyle());
        assertEquals(Weight.NORMAL, settings.fontMono.getWeight());

        assertEquals("Linux Libertine O C", settings.fontHeading.getFamily());
        assertEquals(0.0, settings.fontHeading.getSize(), 0.01); // hm
        assertEquals(Style.NORMAL, settings.fontHeading.getStyle());
        assertEquals(Weight.NORMAL, settings.fontHeading.getWeight());

    }
}
