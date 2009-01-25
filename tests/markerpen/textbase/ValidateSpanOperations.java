/*
 * ValidateSpanOperations.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

import junit.framework.TestCase;

public class ValidateSpanOperations extends TestCase
{
    public final void testSpanStringAccess() {
        final Span one, two;

        one = new StringSpan("Hello World", null);
        assertEquals("Hello World", one.getText());
        assertEquals(0, one.getChar());

        two = new CharacterSpan('£', null);
        assertEquals("£", two.getText());
        assertEquals('£', two.getChar());
    }

    public final void testMetaSpansTextForm() {
        final Span img;

        img = new ImageSpan("images/Logo.png", null);
        assertEquals("", img.getText());
        assertEquals(0, img.getChar());
        assertEquals("images/Logo.png", ((ImageSpan) img).getRef());
    }

    public final void testSpanWidths() {
        final Span c, s, i;

        c = new CharacterSpan('A', null);
        s = new StringSpan("Hello World", null);
        i = new ImageSpan("share/picture.png", null);

        assertEquals(1, c.getWidth());
        assertEquals(11, s.getWidth());
        assertEquals(1, i.getWidth());

    }
}
