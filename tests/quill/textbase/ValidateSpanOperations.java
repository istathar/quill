/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.textbase;

import junit.framework.TestCase;

import static quill.textbase.Span.createSpan;

public class ValidateSpanOperations extends TestCase
{
    public final void testSpanStringAccess() {
        final Span one, two;

        one = createSpan("Hello World", null);
        assertEquals("Hello World", one.getText());
        assertEquals('e', one.getChar(1));

        two = createSpan('£', null);
        assertEquals("£", two.getText());
        assertEquals('£', two.getChar(0));
    }

    public final void testSpanWidths() {
        final Span c, s;

        c = createSpan('A', null);
        s = createSpan("Hello World", null);

        assertTrue(c instanceof CharacterSpan);
        assertEquals(1, c.getWidth());

        assertTrue(s instanceof StringSpan);
        assertEquals(11, s.getWidth());
    }

    public final void testInhibitZeroLengthSpans() {
        final Span c, s, z;

        try {
            c = createSpan("", null);
            fail(c.toString());
        } catch (IllegalArgumentException iae) {
            // good
        }

        s = createSpan("Hello World", null);
        try {
            z = s.split(4, 4);
            fail(z.toString());
        } catch (IllegalArgumentException iae) {
            // good
        }
    }

    public final void testCharacterSpanStringCaching() {
        final Span d1, d2, e1, e2;

        /*
         * Will be cached.
         */
        d1 = createSpan('$', null);
        d2 = createSpan('$', null);

        /*
         * Wouldn't be cached, except that we intern() these after creation.
         */
        e1 = createSpan('€', null);
        e2 = createSpan('€', null);

        assertSame(d1.getText(), d2.getText());
        assertSame(e1.getText(), e2.getText());
    }

    public final void testSpanEquality() {
        final Span c1, c2, s1, s2;

        c1 = createSpan('A', null);
        c2 = createSpan('A', null);
        s1 = createSpan("Hello World", null);
        s2 = createSpan("Hello World", null);

        assertFalse(c1.equals(this));

        assertTrue(c1.equals(c1));
        assertTrue(s1.equals(s1));

        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));
        assertTrue(s1.equals(s2));
        assertTrue(s2.equals(s1));

        assertFalse(c1.equals(s1));
        assertFalse(s1.equals(c1));
    }
}
