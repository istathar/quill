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
import quill.textbase.BlockquoteSegment;
import quill.textbase.DataLayer;
import quill.textbase.ParagraphSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.TextChain;

public class ValidateBlockquoteConversion extends TestCase
{
    public final void testLoadDocbook() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final Series series;
        Segment segment;
        final TextChain text;

        data = new DataLayer();
        data.loadDocument("tests/quill/converter/Blockquote.xml");

        series = data.getActiveDocument().get(0);
        assertEquals(5, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ParagraphSegment);
        segment = series.get(1);
        assertTrue(segment instanceof BlockquoteSegment);
        segment = series.get(2);
        assertTrue(segment instanceof ParagraphSegment);
        segment = series.get(3);
        assertTrue(segment instanceof BlockquoteSegment);
        segment = series.get(4);
        assertTrue(segment instanceof ParagraphSegment);
    }
}
