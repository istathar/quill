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
package quill.textbase;

import junit.framework.TestCase;

public class ValidateSpanOperations extends TestCase
{
    public final void testSpanStringAccess() {
        final Span one, two;

        one = new Span("Hello World", null);
        assertEquals("Hello World", one.getText());
        assertEquals('e', one.getChar(1));

        two = new Span('£', null);
        assertEquals("£", two.getText());
        assertEquals('£', two.getChar(0));
    }

    public final void testSpanWidths() {
        final Span c, s;

        c = new Span('A', null);
        s = new Span("Hello World", null);

        assertEquals(1, c.getWidth());
        assertEquals(11, s.getWidth());
    }

    public final void testCharacterSpanStringCaching() {
        final Span d1, d2, e1, e2;

        /*
         * Will be cached.
         */
        d1 = new Span('$', null);
        d2 = new Span('$', null);

        /*
         * Wouldn't be cached, except that we intern() these after creation.
         */
        e1 = new Span('€', null);
        e2 = new Span('€', null);

        assertSame(d1.getText(), d2.getText());
        assertSame(e1.getText(), e2.getText());
    }
}
