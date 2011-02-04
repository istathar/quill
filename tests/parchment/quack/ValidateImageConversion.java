/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
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
package parchment.quack;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.ImproperFilenameException;
import quill.textbase.Component;
import quill.textbase.FirstSegment;
import quill.textbase.ImageSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

public class ValidateImageConversion extends QuackTestCase
{
    public final void testImageWithCaption() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Component component;
        final Series series;
        Segment segment;

        component = loadDocument("tests/parchment/quack/ImageWithCaption.xml");
        series = component.getSeriesMain();

        assertEquals(1 + 1, series.size());

        segment = series.getSegment(0);
        assertTrue(segment instanceof FirstSegment);

        segment = series.getSegment(1);
        assertTrue(segment instanceof ImageSegment);
        assertEquals("NonExistent.svg", segment.getExtra());

        compareDocument(component);
    }

    public final void testImageWithoutCaption() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Component component;
        final Series series;
        Segment segment;

        component = loadDocument("tests/parchment/quack/ImageWithoutCaption.xml");
        series = component.getSeriesMain();

        assertEquals(1 + 1, series.size());

        segment = series.getSegment(0);
        assertTrue(segment instanceof FirstSegment);

        segment = series.getSegment(1);
        assertTrue(segment instanceof ImageSegment);
        assertEquals("OfNoFixedAddress.svg", segment.getExtra());

        compareDocument(component);
    }
}
