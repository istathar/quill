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

        one = new StringSpan("Hello Devdas", null);
        assertEquals("Hello Devdas", one.getText());
        assertEquals(0, one.getChar());

        two = new CharacterSpan('£', null);
        assertEquals("£", two.getText());
        assertEquals('£', two.getChar());
    }

    public final void testMetaSpansTextForm() {
        final Span img;

        img = new ImageSpan("images/Logo.png", null);
        assertNull(img.getText());
        assertEquals(0, img.getChar());
        assertEquals("images/Logo.png", ((ImageSpan) img).getRef());
    }
}
