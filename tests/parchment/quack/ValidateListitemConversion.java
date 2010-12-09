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
package parchment.quack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.manuscript.Chapter;
import parchment.manuscript.Manuscript;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.ComponentSegment;
import quill.textbase.ListitemSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

public class ValidateListitemConversion extends IOTestCase
{
    public final void testElementToSegmentBullets() throws IOException, ValidityException,
            ParsingException, ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        Segment segment;
        final ByteArrayOutputStream out;
        final String original, result;

        original = loadFileIntoString("tests/parchment/quack/BulletListitems.xml");

        manuscript = new Manuscript();
        manuscript.setFilename("tests/parchment/quack/ValidateListitemConversion.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename("BulletListitems.xml");
        series = chapter.loadDocument();

        assertEquals(1 + 3, series.size());

        segment = series.getSegment(0);
        assertTrue(segment instanceof ComponentSegment);

        segment = series.getSegment(1);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("\u2022", segment.getImage());

        segment = series.getSegment(2);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("\u2022", segment.getImage());

        segment = series.getSegment(3);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("\u2022", segment.getImage());

        out = new ByteArrayOutputStream();
        chapter.saveDocument(series, out);

        result = out.toString();
        assertEquals(original, result);
    }

    public final void testElementToSegmentOrdinals() throws IOException, ValidityException,
            ParsingException, ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        Segment segment;
        final ByteArrayOutputStream out;
        final String original, result;

        original = loadFileIntoString("tests/parchment/quack/OrdinalListitems.xml");

        manuscript = new Manuscript();
        manuscript.setFilename("tests/parchment/quack/ValidateListitemConversion.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename("OrdinalListitems.xml");
        series = chapter.loadDocument();

        assertEquals(1 + 3, series.size());

        segment = series.getSegment(0);
        assertTrue(segment instanceof ComponentSegment);

        segment = series.getSegment(1);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("1", segment.getImage());

        segment = series.getSegment(2);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("2", segment.getImage());

        segment = series.getSegment(3);
        assertTrue(segment instanceof ListitemSegment);
        assertEquals("3", segment.getImage());

        out = new ByteArrayOutputStream();
        chapter.saveDocument(series, out);

        result = out.toString();
        assertEquals(original, result);
    }
}
