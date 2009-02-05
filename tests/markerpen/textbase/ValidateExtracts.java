/*
 * ValidateExtracts.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package markerpen.textbase;

import junit.framework.TestCase;

/**
 * Make sure the extraction and reversing properties of Extract and the
 * extractRange() which creates them hold.
 * 
 * @author Andrew Cowie
 */
public class ValidateExtracts extends TestCase
{
    public final void testExtractRange() {
        final Text text;
        Extract extract;

        text = new Text();
        text.append(new StringSpan("Hello World", null));

        extract = text.extractRange(1, 3);
        assertEquals("Hello World", text.toString());
        assertEquals(1, extract.range.length);
        assertEquals("ell", extract.range[0].getText());

        extract = text.extractRange(0, 11);
        assertEquals(3, extract.range.length);
        assertEquals("H", extract.range[0].getText());
        assertEquals("ell", extract.range[1].getText());
        assertEquals("o World", extract.range[2].getText());
    }

    public final void testWidthNegative() {
        final Text text;
        Extract extract;

        text = new Text();
        text.append(new StringSpan("Hello World", null));

        extract = text.extractRange(9, -2);
        assertEquals(1, extract.range.length);
        assertEquals("or", extract.range[0].getText());
    }
}
