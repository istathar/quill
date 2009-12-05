/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
import quill.client.IOTestCase;
import quill.textbase.Change;
import quill.textbase.Common;
import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.textbase.InsertTextualChange;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateProperNewlineHandling extends IOTestCase
{
    public final void testWriteChainWithTrainingBlankLine() throws IOException {
        final TextChain chain;
        final DataLayer data;
        Span[] spans;
        int i, j;
        Span span;
        Change change;
        final Folio folio;
        final Series series;
        final Segment segment;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a trivial example
         */

        data = new DataLayer();
        data.createDocument();

        folio = data.getActiveDocument();
        series = folio.get(0);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        chain = segment.getText();

        spans = new Span[] {
                createSpan("Hello\n", null),
                createSpan("\n", null),
                createSpan("World", Common.BOLD),
                createSpan("\n", null),
                createSpan("\n", null)
        };
        j = 0;
        for (i = 0; i < spans.length; i++) {
            span = spans[i];
            change = new InsertTextualChange(chain, j, span);
            data.apply(change);
            j += span.getWidth();
        }

        /*
         * Now run conversion process.
         */

        out = new ByteArrayOutputStream();
        data.saveDocument(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
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
            ParsingException {
        final DataLayer data;

        data = new DataLayer();
        try {
            data.loadDocument("tests/quill/quack/IllegalNewlines.xml");
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
    public final void testBugOverlyLongInline() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final ByteArrayOutputStream out;
        final String expected;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/ReallyLongInlineLeadingBlock.xml");

        out = new ByteArrayOutputStream();
        data.saveDocument(out);

        expected = loadFileIntoString("tests/quill/quack/ReallyLongInlineLeadingBlock.xml");
        assertEquals(expected, out.toString());
    }
}
