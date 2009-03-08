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

import quill.textbase.CharacterSpan;
import quill.textbase.ImageSpan;
import quill.textbase.Span;
import quill.textbase.StringSpan;
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

    public final void testCharacterSpanStringCaching() {
        final Span d1, d2, e1, e2;

        /*
         * Will be cached.
         */
        d1 = new CharacterSpan('$', null);
        d2 = new CharacterSpan('$', null);

        /*
         * Wouldn't be cached, except that we intern() these after creation.
         */
        e1 = new CharacterSpan('€', null);
        e2 = new CharacterSpan('€', null);

        assertSame(d1.getText(), d2.getText());
        assertSame(e1.getText(), e2.getText());
    }
}
