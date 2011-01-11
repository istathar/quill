/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2011 Operational Dynamics Consulting, Pty Ltd
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
            new ChapterSegment(blank),
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
        assertTrue(series.getSegment(2) instanceof NormalSegment);
    }

    public final void testSeriesInsertMid() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.getSegment(2) instanceof NormalSegment);

        after = before.insert(2, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertTrue(after.getSegment(1) instanceof HeadingSegment);
        assertTrue(after.getSegment(2) instanceof PreformatSegment);
        assertTrue(after.getSegment(3) instanceof NormalSegment);
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertSame(after.getSegment(3), segments[2]);
        assertSame(after.getSegment(4), segments[3]);
        assertSame(after.getSegment(5), segments[4]);
    }

    public final void testSeriesInsertEnd() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.getSegment(4) instanceof NormalSegment);

        after = before.insert(5, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertSame(after.getSegment(2), segments[2]);
        assertSame(after.getSegment(3), segments[3]);
        assertSame(after.getSegment(4), segments[4]);
        assertTrue(after.getSegment(5) instanceof PreformatSegment);
    }

    public final void testSeriesInsertBegin() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        assertEquals(5, before.size());
        assertTrue(before.getSegment(0) instanceof ChapterSegment);

        after = before.insert(0, new PreformatSegment(blank));

        assertEquals(6, after.size());
        assertTrue(after.getSegment(0) instanceof PreformatSegment);
        assertSame(after.getSegment(1), segments[0]);
        assertSame(after.getSegment(2), segments[1]);
        assertSame(after.getSegment(3), segments[2]);
        assertSame(after.getSegment(4), segments[3]);
        assertSame(after.getSegment(5), segments[4]);
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
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertSame(after.getSegment(2), segments[3]);
        assertSame(after.getSegment(3), segments[4]);
    }

    public final void testSeriesDeleteEnd() {
        final Series before, after;

        before = new Series(segments);

        after = before.delete(4);

        assertEquals(4, after.size());
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertSame(after.getSegment(2), segments[2]);
        assertSame(after.getSegment(3), segments[3]);
    }

    public final void testSeriesDeleteBegin() {
        final Series before, after;

        before = new Series(segments);

        after = before.delete(0);

        assertEquals(4, after.size());
        assertSame(after.getSegment(0), segments[1]);
        assertSame(after.getSegment(1), segments[2]);
        assertSame(after.getSegment(2), segments[3]);
        assertSame(after.getSegment(3), segments[4]);
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
     * allowed to splice the leading FirstSegment.
     */
    public final void testSeriesSpliceBegin() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);

        assertEquals(5, before.size());
        assertTrue(before.getSegment(0) instanceof ChapterSegment);
        assertTrue(before.getSegment(1) instanceof HeadingSegment);
        assertSame(before.getSegment(1), segments[1]);

        after = before.splice(0, new ChapterSegment(blank), new QuoteSegment(blank), new ChapterSegment(
                blank));

        assertEquals(7, after.size());
        assertTrue(after.getSegment(0) instanceof ChapterSegment);
        assertTrue(after.getSegment(1) instanceof QuoteSegment);
        assertTrue(after.getSegment(2) instanceof ChapterSegment);
        assertSame(after.getSegment(3), segments[1]);
        assertSame(after.getSegment(4), segments[2]);
        assertSame(after.getSegment(5), segments[3]);
        assertSame(after.getSegment(6), segments[4]);
    }

    public final void testSeriesSpliceMid() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.getSegment(2) instanceof NormalSegment);

        after = before.splice(2, new NormalSegment(blank), new QuoteSegment(blank), new NormalSegment(
                blank));

        assertEquals(7, after.size());
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertTrue(after.getSegment(2) instanceof NormalSegment);
        assertTrue(after.getSegment(3) instanceof QuoteSegment);
        assertTrue(after.getSegment(4) instanceof NormalSegment);
        assertSame(after.getSegment(5), segments[3]);
        assertSame(after.getSegment(6), segments[4]);
    }

    public final void testSeriesSpliceMidWithText() {
        Span span1, span2, span3;
        final Series before, after;
        final Extract one, two, three;
        final Segment une, deux, trois;

        span1 = Span.createSpan("This is a test", null);
        one = Extract.create(span1);

        span2 = Span.createSpan("DO REI ME", null);
        two = Extract.create(span2);

        span3 = Span.createSpan(" of the emergency broadcast sytem", null);
        three = Extract.create(span3);

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.getSegment(2) instanceof NormalSegment);

        une = new NormalSegment(one);

        deux = new QuoteSegment(two);
        trois = new NormalSegment(three);

        after = before.splice(2, une, deux, trois);

        assertEquals(7, after.size());
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertTrue(after.getSegment(2) instanceof NormalSegment);
        assertSame(after.getSegment(2), une);
        assertTrue(after.getSegment(3) instanceof QuoteSegment);
        assertSame(after.getSegment(3), deux);
        assertTrue(after.getSegment(4) instanceof NormalSegment);
        assertSame(after.getSegment(4), trois);
        assertSame(after.getSegment(5), segments[3]);
        assertSame(after.getSegment(6), segments[4]);
    }

    public final void testSeriesSpliceEnd() {
        final Series before, after;
        final Extract blank;

        blank = Extract.create();

        before = new Series(segments);
        assertEquals(5, before.size());
        assertTrue(before.getSegment(4) instanceof NormalSegment);

        after = before.splice(4, new NormalSegment(blank), new QuoteSegment(blank), new NormalSegment(
                blank));

        assertEquals(7, after.size());
        assertSame(after.getSegment(0), segments[0]);
        assertSame(after.getSegment(1), segments[1]);
        assertSame(after.getSegment(2), segments[2]);
        assertSame(after.getSegment(3), segments[3]);
        assertTrue(after.getSegment(4) instanceof NormalSegment);
        assertTrue(after.getSegment(5) instanceof QuoteSegment);
        assertTrue(after.getSegment(6) instanceof NormalSegment);
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
