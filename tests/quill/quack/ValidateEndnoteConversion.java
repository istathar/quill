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
package quill.quack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.format.Chapter;
import quill.client.IOTestCase;
import quill.textbase.ComponentSegment;
import quill.textbase.Extract;
import quill.textbase.MarkerSpan;
import quill.textbase.Markup;
import quill.textbase.NormalSegment;
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.SpanVisitor;
import quill.textbase.Special;
import quill.textbase.StringSpan;
import quill.textbase.TextChain;

public class ValidateEndnoteConversion extends IOTestCase
{
    public final void testMarkerSpan() {
        Span span;

        span = Span.createMarker("1", Special.NOTE);
        assertTrue(span instanceof MarkerSpan);
        assertEquals("1", span.getText());
        assertEquals(Special.NOTE, span.getMarkup());
    }

    public final void testInlineNote() throws IOException, ValidityException, ParsingException {
        final Chapter chapter;
        final Series series;
        Segment segment;
        final TextChain chain;
        final Extract entire;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String original, result;

        original = loadFileIntoString("tests/quill/quack/Endnote.xml");

        chapter = new Chapter();
        chapter.setFilename("tests/quill/quack/Endnote.xml");
        series = chapter.loadDocument();

        assertEquals(2, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof QuoteSegment);

        chain = segment.getText();
        entire = chain.extractAll();
        entire.visit(new SpanVisitor() {
            private int i = 0;

            public boolean visit(Span span) {
                switch (i) {
                case 0:
                    assertTrue(span instanceof StringSpan);
                    break;
                case 1:
                    assertTrue(span instanceof MarkerSpan);
                    assertEquals(Special.NOTE, span.getMarkup());
                    break;
                default:
                    fail();
                }
                i++;
                return false;
            }
        });

        converter = new QuackConverter();
        converter.append(new ComponentSegment());
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        result = out.toString();
        assertEquals(original, result);
    }

    public final void testManyNotes() throws IOException, ValidityException, ParsingException {
        final String FILE;
        final Chapter chapter;
        final Series series;
        Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String original, result;
        int i;

        FILE = "tests/quill/quack/Manynotes.xml";

        original = loadFileIntoString(FILE);

        chapter = new Chapter();
        chapter.setFilename(FILE);

        /*
         * Check the state is what we think it is
         */

        series = chapter.loadDocument();
        assertEquals(4, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        segment = series.get(2);
        assertTrue(segment instanceof QuoteSegment);
        segment = series.get(3);
        assertTrue(segment instanceof NormalSegment);

        /*
         * Now, write out, and test.
         */

        converter = new QuackConverter();
        converter.append(new ComponentSegment());

        for (i = 1; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        result = out.toString();
        assertEquals(original, result);
    }

    /*
     * Bug: for some reason, markup at the beginning of a following block is
     * being lost. This test demonstrates the problem.
     */
    public final void testTwoBlockWithNote() throws IOException, ValidityException, ParsingException {
        final String FILE1, FILE2;
        final Chapter chapter;
        final Series series;
        Segment segment;
        final TextChain chain;
        Span span;
        final int offset;
        final Markup markup;
        final Extract extract;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String expected, result;
        int i;

        FILE1 = "tests/quill/quack/Manynotes.xml";
        FILE2 = "tests/quill/quack/Manynotes2.xml";

        expected = loadFileIntoString(FILE2);

        chapter = new Chapter();
        chapter.setFilename(FILE1);

        /*
         * Check the state is what we think it is
         */

        series = chapter.loadDocument();
        assertEquals(4, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        segment = series.get(2);
        assertTrue(segment instanceof QuoteSegment);
        segment = series.get(3);
        assertTrue(segment instanceof NormalSegment);

        /*
         * Append a <note> to the end of the first NormalSegment
         */

        segment = series.get(1);
        chain = segment.getText();

        span = Span.createMarker("[Einstein, 1905]", Special.NOTE);
        chain.append(span);

        /*
         * Make sure it got added
         */

        offset = chain.length();
        markup = chain.getMarkupAt(offset - 1);
        assertEquals(Special.NOTE, markup);

        extract = chain.extractRange(offset - 1, 1);
        extract.visit(new SpanVisitor() {
            public boolean visit(Span span) {
                assertTrue(span instanceof MarkerSpan);
                return true;
            }
        });

        /*
         * Now, write out, and test. We shouldn't have lost any markup.
         */

        converter = new QuackConverter();
        converter.append(new ComponentSegment());

        for (i = 1; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        result = out.toString();
        assertEquals(expected, result);
    }
}
