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
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.InsertTextualChange;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.SpanVisitor;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

/**
 * Tests for bugs & fixes in and around serializing and loading.
 * 
 * @author Andrew Cowie
 */
public class ValidateDataIntegrity extends IOTestCase
{
    private static final void insertThreeSpansIntoFirstSegment(final DataLayer data) {
        final TextChain chain;
        Span[] spans;
        int i, j;
        Span span;
        Change change;
        final Folio folio;
        final Series series;
        final Segment segment;

        folio = data.getActiveDocument();
        series = folio.get(0);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        chain = segment.getText();

        spans = new Span[] {
                createSpan("Hello ", Common.BOLD),
                createSpan("GtkButton", Common.TYPE),
                createSpan(" world", Common.BOLD)
        };
        j = 0;
        for (i = 0; i < spans.length; i++) {
            span = spans[i];
            change = new InsertTextualChange(chain, j, span);
            data.apply(change);
            j += span.getWidth();
        }
    }

    /*
     * Bug where markup1markup2markup3 is loosing markup2!
     */
    public final void testContinuousMarkupChangesSave() throws IOException {
        final DataLayer data;
        final ByteArrayOutputStream out;
        final String expected;

        data = new DataLayer();
        data.createDocument();

        insertThreeSpansIntoFirstSegment(data);

        out = new ByteArrayOutputStream();
        data.saveDocument(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                "<bold>Hello </bold><type>GtkButton</type><bold>",
                "world</bold>",
                "</text>",
                "</chapter>"
        });

        assertEquals(expected, out.toString());
    }

    /*
     * This test echoes the proceeding one, but verifies that the same problem
     * does not occur when loading; we're also loosing whitespace if the wrap
     * boundary occurs at a continuous markup joint.
     */
    public final void testContinuousMarkupChangesLoad() throws IOException, ValidityException,
            ParsingException {
        final DataLayer data;
        final Span[] expected;
        final Folio folio;
        final Series series;
        final Segment segment;
        final TextChain chain;
        final Extract entire;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/ContinuousMarkup.xml");

        expected = new Span[] {
                createSpan("Hello ", Common.BOLD),
                createSpan("GtkButton", Common.TYPE),
                createSpan(" world", Common.BOLD),
                createSpan(" ", Common.BOLD), // the \n
                createSpan("printf()", Common.LITERAL)
        };

        folio = data.getActiveDocument();
        series = folio.get(0);
        segment = series.get(1);
        chain = segment.getText();
        entire = chain.extractAll();
        assertNotNull(entire);

        entire.visit(new SpanVisitor() {
            private int i = 0;

            public boolean visit(Span span) {
                assertEquals(expected[i], span);
                i++;
                return false;
            }
        });
    }

    public void testMarkupContinuingBetweenTextBlocks() throws ValidityException, ParsingException,
            IOException {
        final DataLayer data;
        final Span[] inbound;
        final Folio folio;
        final Series series;
        final Segment segment;
        final TextChain chain;
        final Extract entire;
        final ByteArrayOutputStream out;
        final String outbound;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/TwoBlocksMarkup.xml");

        inbound = new Span[] {
                createSpan("Hello world. ", null),
                createSpan("It is a lovely day.", Common.ITALICS),
                createSpan("\n", null),
                createSpan("And so is this day.", Common.ITALICS),
                createSpan(" Goodbye.", null),
        };

        folio = data.getActiveDocument();
        series = folio.get(0);
        segment = series.get(1);
        chain = segment.getText();
        entire = chain.extractAll();
        assertNotNull(entire);

        entire.visit(new SpanVisitor() {
            private int i = 0;

            public boolean visit(Span span) {
                assertEquals(inbound[i], span);
                i++;
                return false;
            }
        });

        out = new ByteArrayOutputStream();
        data.saveDocument(out);

        outbound = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                "Hello world. <italics>It is a lovely day.</italics>",
                "</text>",
                "<text>",
                "<italics>And so is this day.</italics> Goodbye.",
                "</text>",
                "</chapter>"
        });

        assertEquals(outbound, out.toString());
    }
}
