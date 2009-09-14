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
package quill.converter;

import java.io.IOException;

import junit.framework.TestCase;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.textbase.ComponentSegment;
import quill.textbase.DataLayer;
import quill.textbase.NormalSegment;
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;

public class ValidateBlockquoteConversion extends TestCase
{
    public final void testLoadDocbook() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final Series series;
        Segment segment;

        data = new DataLayer();
        data.loadDocument("tests/quill/converter/Blockquote.xml");

        series = data.getActiveDocument().get(0);
        assertEquals(4, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        segment = series.get(2);
        assertTrue(segment instanceof QuoteSegment);
        segment = series.get(3);
        assertTrue(segment instanceof NormalSegment);
    }
}
