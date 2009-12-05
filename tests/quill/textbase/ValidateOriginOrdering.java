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
