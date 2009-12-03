/*
 * ValidateWrapperExpansions.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package quill.textbase;

import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.TestCase;

public class ValidateOriginOrdering extends TestCase
{
    public final void testSeriesDeleteOvershoot() {
        final Origin zero, one, two, three, four, five;
        final TreeSet<Origin> set;
        final Iterator<Origin> iter;
        Origin origin;

        zero = new Origin(0, 0);
        one = new Origin(0, 25);
        two = new Origin(1, 0);
        three = new Origin(1, 25);
        four = new Origin(1, 60);
        five = new Origin(2, 33);

        /*
         * Spot check comparable behaviour
         */

        assertEquals(-1, zero.compareTo(one));
        assertEquals(1, one.compareTo(zero));
        assertEquals(0, zero.compareTo(zero));
        assertEquals(0, four.compareTo(four));

        assertEquals(-1, four.compareTo(five));
        assertEquals(1, five.compareTo(four));

        assertTrue(two.equals(two));
        assertFalse(zero.equals(two));
        assertFalse(two.equals(zero));

        assertFalse(one.equals(three));
        assertFalse(three.equals(one));

        /*
         * Now test natural ordering
         */

        set = new TreeSet<Origin>();

        set.add(three);
        set.add(two);
        set.add(four);
        set.add(zero);
        set.add(five);
        set.add(one);

        iter = set.iterator();

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(zero, origin);

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(one, origin);

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(two, origin);

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(three, origin);

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(four, origin);

        assertTrue(iter.hasNext());
        origin = iter.next();
        assertSame(five, origin);

        assertFalse(iter.hasNext());
    }
}
