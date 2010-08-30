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

public class ValidateWrapperExpansions extends TestCase
{
    private Segment[] segments;

    public void setUp() {
        final Extract blank;

        blank = Extract.create();

        segments = new Segment[] {
                new ComponentSegment(blank),
                new HeadingSegment(blank),
                new NormalSegment(blank),
                new PreformatSegment(blank),
                new NormalSegment(blank),
        };
    }

    public final void testSeriesStart() {
        final Series series;

        series = new Series(segments);
        assertEquals(5, series.size());
        assertTrue(series.get(2) instanceof NormalSegment);
    }

    public final void testSeriesInsertMid() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.get(2) instanceof NormalSegment);

        after = before.insert(2, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertTrue(after.get(1) instanceof HeadingSegment);
        assertTrue(after.get(2) instanceof PreformatSegment);
        assertTrue(after.get(3) instanceof NormalSegment);
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(3), segments[2]);
        assertSame(after.get(4), segments[3]);
        assertSame(after.get(5), segments[4]);
    }

    public final void testSeriesInsertEnd() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.get(4) instanceof NormalSegment);

        after = before.insert(5, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(2), segments[2]);
        assertSame(after.get(3), segments[3]);
        assertSame(after.get(4), segments[4]);
        assertTrue(after.get(5) instanceof PreformatSegment);
    }

    public final void testSeriesInsertBegin() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        assertEquals(5, before.size());
        assertTrue(before.get(0) instanceof ComponentSegment);

        after = before.insert(0, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertTrue(after.get(0) instanceof PreformatSegment);
        assertSame(after.get(1), segments[0]);
        assertSame(after.get(2), segments[1]);
        assertSame(after.get(3), segments[2]);
        assertSame(after.get(4), segments[3]);
        assertSame(after.get(5), segments[4]);
    }

    public final void testSeriesInsertUndershoot() {
        final Series before;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        try {
            before.insert(-1, new PreformatSegment(blank));
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesInsertOvershoot() {
        final Series before;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        try {
            before.insert(6, new PreformatSegment(blank));
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteMid() {
        final Series before, after;

        before = new Series(segments);

        after = before.delete(2);

        assertEquals(4, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(2), segments[3]);
        assertSame(after.get(3), segments[4]);
    }

    public final void testSeriesDeleteEnd() {
        final Series before, after;

        before = new Series(segments);

        after = before.delete(4);

        assertEquals(4, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(2), segments[2]);
        assertSame(after.get(3), segments[3]);
    }

    public final void testSeriesDeleteBegin() {
        final Series before, after;

        before = new Series(segments);

        after = before.delete(0);

        assertEquals(4, after.size());
        assertSame(after.get(0), segments[1]);
        assertSame(after.get(1), segments[2]);
        assertSame(after.get(2), segments[3]);
        assertSame(after.get(3), segments[4]);
    }

    public final void testSeriesDeleteUndershoot() {
        final Series before;

        before = new Series(segments);

        try {
            before.delete(-1);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteOvershoot() {
        final Series before;

        before = new Series(segments);

        try {
            before.delete(5);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    /*
     * This is quite contrived, since in standard components you are NOT
     * allowed to splice the leading ComponentSegment.
     */
    public final void testSeriesSpliceBegin() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        assertEquals(5, before.size());
        assertTrue(before.get(0) instanceof ComponentSegment);
        assertTrue(before.get(1) instanceof HeadingSegment);

        after = before.splice(0, new ComponentSegment(blank), new QuoteSegment(blank),
                new ComponentSegment(blank));

        assertEquals(7, after.size());
        assertTrue(after.get(0) instanceof ComponentSegment);
        assertTrue(after.get(1) instanceof QuoteSegment);
        assertTrue(after.get(2) instanceof ComponentSegment);
        assertSame(after.get(3), segments[1]);
        assertSame(after.get(4), segments[2]);
        assertSame(after.get(5), segments[3]);
        assertSame(after.get(6), segments[4]);
    }

    public final void testSeriesSpliceMid() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.get(2) instanceof NormalSegment);

        after = before.splice(2, new NormalSegment(blank), new QuoteSegment(blank), new NormalSegment(
                blank));

        assertEquals(7, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertTrue(after.get(2) instanceof NormalSegment);
        assertTrue(after.get(3) instanceof QuoteSegment);
        assertTrue(after.get(4) instanceof NormalSegment);
        assertSame(after.get(5), segments[3]);
        assertSame(after.get(6), segments[4]);
    }

    public final void testSeriesSpliceEnd() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.get(4) instanceof NormalSegment);

        after = before.splice(4, new NormalSegment(blank), new QuoteSegment(blank), new NormalSegment(
                blank));

        assertEquals(7, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(2), segments[2]);
        assertSame(after.get(3), segments[3]);
        assertTrue(after.get(4) instanceof NormalSegment);
        assertTrue(after.get(5) instanceof QuoteSegment);
        assertTrue(after.get(6) instanceof NormalSegment);
    }

    public final void testSeriesSpliceUndershoot() {
        final Series before;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        try {
            before.splice(-1, new NormalSegment(blank), new QuoteSegment(blank),
                    new NormalSegment(blank));

            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesSpliceOvershoot() {
        final Series before;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        try {
            before.splice(6, new NormalSegment(blank), new QuoteSegment(blank), new NormalSegment(blank));

            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

}
