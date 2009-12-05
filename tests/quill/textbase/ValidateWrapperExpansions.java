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
        segments = new Segment[] {
                new ComponentSegment(),
                new HeadingSegment(),
                new NormalSegment(),
                new PreformatSegment(),
                new NormalSegment(),
        };
    }

    public final void testSeriesStart() {
        final Series series;

        series = new Series(segments);
        assertEquals(5, series.size());
        assertTrue(series.get(2) instanceof NormalSegment);
    }

    public final void testSeriesInsertMid() {
        final Series series;

        series = new Series(segments);
        assertEquals(5, series.size());
        assertTrue(series.get(2) instanceof NormalSegment);

        series.insert(2, new PreformatSegment());

        assertEquals(6, series.size());
        assertTrue(series.get(1) instanceof HeadingSegment);
        assertTrue(series.get(2) instanceof PreformatSegment);
        assertTrue(series.get(3) instanceof NormalSegment);
        assertSame(series.get(0), segments[0]);
        assertSame(series.get(1), segments[1]);
        assertSame(series.get(3), segments[2]);
        assertSame(series.get(4), segments[3]);
        assertSame(series.get(5), segments[4]);
    }

    public final void testSeriesInsertEnd() {
        final Series series;

        series = new Series(segments);
        assertEquals(5, series.size());
        assertTrue(series.get(4) instanceof NormalSegment);

        series.insert(5, new PreformatSegment());

        assertEquals(6, series.size());
        assertSame(series.get(0), segments[0]);
        assertSame(series.get(1), segments[1]);
        assertSame(series.get(2), segments[2]);
        assertSame(series.get(3), segments[3]);
        assertSame(series.get(4), segments[4]);
        assertTrue(series.get(5) instanceof PreformatSegment);
    }

    public final void testSeriesInsertBegin() {
        final Series series;

        series = new Series(segments);

        assertEquals(5, series.size());
        assertTrue(series.get(0) instanceof ComponentSegment);

        series.insert(0, new PreformatSegment());

        assertEquals(6, series.size());
        assertTrue(series.get(0) instanceof PreformatSegment);
        assertSame(series.get(1), segments[0]);
        assertSame(series.get(2), segments[1]);
        assertSame(series.get(3), segments[2]);
        assertSame(series.get(4), segments[3]);
        assertSame(series.get(5), segments[4]);
    }

    public final void testSeriesInsertUndershoot() {
        final Series series;

        series = new Series(segments);

        try {
            series.insert(-1, new PreformatSegment());
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesInsertOvershoot() {
        final Series series;

        series = new Series(segments);

        try {
            series.insert(6, new PreformatSegment());
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteMid() {
        final Series series;

        series = new Series(segments);

        series.delete(2);

        assertEquals(4, series.size());
        assertSame(series.get(0), segments[0]);
        assertSame(series.get(1), segments[1]);
        assertSame(series.get(2), segments[3]);
        assertSame(series.get(3), segments[4]);
    }

    public final void testSeriesDeleteEnd() {
        final Series series;

        series = new Series(segments);

        series.delete(4);

        assertEquals(4, series.size());
        assertSame(series.get(0), segments[0]);
        assertSame(series.get(1), segments[1]);
        assertSame(series.get(2), segments[2]);
        assertSame(series.get(3), segments[3]);
    }

    public final void testSeriesDeleteBegin() {
        final Series series;

        series = new Series(segments);

        series.delete(0);

        assertEquals(4, series.size());
        assertSame(series.get(0), segments[1]);
        assertSame(series.get(1), segments[2]);
        assertSame(series.get(2), segments[3]);
        assertSame(series.get(3), segments[4]);
    }

    public final void testSeriesDeleteUndershoot() {
        final Series series;

        series = new Series(segments);

        try {
            series.delete(-1);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteOvershoot() {
        final Series series;

        series = new Series(segments);

        try {
            series.delete(5);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }
}
