/*
 * ValidateDataIntegrity.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
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
        final ByteArrayOutputStream out;
        final String expected;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/ContinuousMarkup.xml");

        out = new ByteArrayOutputStream();
        data.saveDocument(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                "<bold>Hello </bold><type>GtkButton</type><bold>",
                "world </bold><literal>printf()</literal>",
                "</text>",
                "</chapter>"
        });

        assertEquals(expected, out.toString());
    }

}
