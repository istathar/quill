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
package parchment.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.ImproperFilenameException;
import quill.textbase.Common;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateProperNewlineHandling extends ParchmentTestCase
{
    public final void testWriteChainWithTrainingBlankLine() throws IOException {
        final TextChain chain;
        Extract entire;
        final Manuscript manuscript;
        final Chapter chapter;
        Span[] spans;
        int i;
        Span span;
        final Folio folio;
        Series series;
        Segment segment;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a trivial example
         */

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        series = folio.getSeries(0);
        segment = series.getSegment(1);
        assertTrue(segment instanceof NormalSegment);
        entire = segment.getEntire();

        spans = new Span[] {
            createSpan("Hello\n", null),
            createSpan("\n", null),
            createSpan("World", Common.BOLD),
            createSpan("\n", null),
            createSpan("\n", null)
        };

        chain = new TextChain();
        for (i = 0; i < spans.length; i++) {
            span = spans[i];
            chain.append(span);
        }

        /*
         * Now run conversion process.
         */
        chapter = new Chapter(manuscript);

        out = new ByteArrayOutputStream();
        entire = chain.extractAll();
        segment = segment.createSimilar(entire, 0, 0, entire.getWidth());
        series = series.update(1, segment);

        chapter.saveDocument(series, out);

        blob = combine(new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.4\">",
            "<text>",
            "Hello",
            "</text>",
            "<text/>",
            "<text>",
            "<bold>World</bold>",
            "</text>",
            "<text/>",
            "<text/>",
            "</chapter>"
        });
        assertEquals(blob, out.toString());
    }

    public final void testCatchDocumentKnownBroken() throws IOException, ValidityException,
            ParsingException, ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/quill/quack/ValudateProperNewlineHandling.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename("IllegalNewlines.xml");
        try {
            chapter.loadDocument();
            fail();
            return;
        } catch (IllegalStateException ise) {
            // good
        }
    }

    /*
     * The bug turns out to be that if an inline with a really long
     * (unwrappable) body leads a block, then an additional bare newline was
     * appearing, breaking the contract that we don't have bare newlines in
     * non-preformatted elements
     */
    public final void testBugOverlyLongInline() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        final ByteArrayOutputStream out;
        final String expected;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/quill/quack/ValudateProperNewlineHandling.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename("ReallyLongInlineLeadingBlock.xml");
        series = chapter.loadDocument();

        out = new ByteArrayOutputStream();
        chapter.saveDocument(series, out);

        expected = loadFileIntoString("tests/quill/quack/ReallyLongInlineLeadingBlock.xml");
        assertEquals(expected, out.toString());
    }
}
