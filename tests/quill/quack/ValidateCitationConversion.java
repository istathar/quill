/*
 * ValidateCitationConversion.java
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
import quill.textbase.ComponentSegment;
import quill.textbase.DataLayer;
import quill.textbase.Extract;
import quill.textbase.MarkerSpan;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.Special;
import quill.textbase.StringSpan;
import quill.textbase.TextChain;

public class ValidateCitationConversion extends IOTestCase
{
    public final void testMarkerSpan() {
        Span span;

        span = Span.createMarker("[Penrose 1989]", Special.CITE);
        assertTrue(span instanceof MarkerSpan);
        assertEquals("[Penrose 1989]", span.getText());
        assertEquals(Special.CITE, span.getMarkup());
    }

    public final void testInlineCite() throws IOException, ValidityException, ParsingException {
        final String FILE;
        final DataLayer data;
        final Series series;
        Segment segment;
        final TextChain chain;
        final Extract entire;
        Span span;
        final QuackConverter converter;
        int i;
        final ByteArrayOutputStream out;
        final String original, result;

        FILE = "tests/quill/quack/Citation.xml";

        original = loadFileIntoString(FILE);

        data = new DataLayer();
        data.loadDocument(FILE);

        series = data.getActiveDocument().get(0);
        assertEquals(2, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);

        chain = segment.getText();
        entire = chain.extractAll();
        assertEquals(2, entire.size());
        span = entire.get(0);
        assertTrue(span instanceof StringSpan);
        span = entire.get(1);
        assertTrue(span instanceof MarkerSpan);
        assertEquals(Special.CITE, span.getMarkup());

        converter = new QuackConverter();

        for (i = 0; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        result = out.toString();
        assertEquals(original, result);
    }
}
