/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import junit.framework.TestCase;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.format.Chapter;
import parchment.format.Manuscript;
import quill.client.ImproperFilenameException;
import quill.textbase.ChapterSegment;
import quill.textbase.NormalSegment;
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

public class ValidateBlockquoteConversion extends TestCase
{
    public final void testLoadSequences() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        Segment segment;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/parchment/quack/ValidateBlockquoteConversion.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename("Blockquote.xml");
        series = chapter.loadDocument();

        assertEquals(4, series.size());

        /*
         * Automatically added on load
         */

        segment = series.getSegment(0);
        assertTrue(segment instanceof ChapterSegment);

        /*
         * Actual blocks from file
         */
        segment = series.getSegment(1);
        assertTrue(segment instanceof NormalSegment);
        segment = series.getSegment(2);
        assertTrue(segment instanceof QuoteSegment);
        segment = series.getSegment(3);
        assertTrue(segment instanceof NormalSegment);
    }
}
