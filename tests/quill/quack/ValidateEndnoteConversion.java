/*
 * ValidateBlockquoteConversion.java
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
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

public class ValidateEndnoteConversion extends IOTestCase
{
    public final void testInlineNote() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final Series series;
        Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/Endnote.xml");

        series = data.getActiveDocument().get(0);
        assertEquals(2, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof QuoteSegment);

        converter = new QuackConverter();
        converter.append(new ComponentSegment());
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<quote>",
                "In the beginning...",
                "</quote>",
                "</chapter>"
        });
        assertEquals(blob, out.toString());
    }
}
