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

import junit.framework.TestCase;

public class ValidateWrapperExpansions extends TestCase
{
    private Series start;

    public void setUp() {
        start = new Series(new Segment[] {
                new ComponentSegment(),
                new HeadingSegment(),
                new ParagraphSegment(),
                new PreformatSegment(),
                new ParagraphSegment(),
        });
    }

    public final void testSeriesStart() {
        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);
    }

    public final void testSeriesInsertMid() {
        final Series result;

        result = start.insert(2, new PreformatSegment());

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(6, result.size());
        assertTrue(result.get(1) instanceof HeadingSegment);
        assertTrue(result.get(2) instanceof PreformatSegment);
        assertTrue(result.get(3) instanceof ParagraphSegment);
        assertSame(result.get(0), start.get(0));
        assertSame(result.get(1), start.get(1));
        assertSame(result.get(3), start.get(2));
        assertSame(result.get(4), start.get(3));
        assertSame(result.get(5), start.get(4));
    }

    public final void testSeriesInsertEnd() {
        final Series result;

        result = start.insert(5, new PreformatSegment());

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(6, result.size());

        assertSame(result.get(0), start.get(0));
        assertSame(result.get(1), start.get(1));
        assertSame(result.get(2), start.get(2));
        assertSame(result.get(3), start.get(3));
        assertSame(result.get(4), start.get(4));
        assertTrue(result.get(5) instanceof PreformatSegment);
    }

    public final void testSeriesInsertBegin() {
        final Series result;

        result = start.insert(0, new PreformatSegment());

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(6, result.size());

        assertTrue(result.get(0) instanceof PreformatSegment);
        assertSame(result.get(1), start.get(0));
        assertSame(result.get(2), start.get(1));
        assertSame(result.get(3), start.get(2));
        assertSame(result.get(4), start.get(3));
        assertSame(result.get(5), start.get(4));
    }

    public final void testSeriesInsertUndershoot() {
        try {
            start.insert(-1, new PreformatSegment());
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesInsertOvershoot() {
        try {
            start.insert(6, new PreformatSegment());
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteMid() {
        final Series result;

        result = start.delete(2);

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(4, result.size());
        assertSame(result.get(0), start.get(0));
        assertSame(result.get(1), start.get(1));
        assertSame(result.get(2), start.get(3));
        assertSame(result.get(3), start.get(4));
    }

    public final void testSeriesDeleteEnd() {
        final Series result;

        result = start.delete(4);

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(4, result.size());
        assertSame(result.get(0), start.get(0));
        assertSame(result.get(1), start.get(1));
        assertSame(result.get(2), start.get(2));
        assertSame(result.get(3), start.get(3));
    }

    public final void testSeriesDeleteBegin() {
        final Series result;

        result = start.delete(0);

        assertEquals(5, start.size());
        assertTrue(start.get(2) instanceof ParagraphSegment);

        assertEquals(4, result.size());
        assertSame(result.get(0), start.get(1));
        assertSame(result.get(1), start.get(2));
        assertSame(result.get(2), start.get(3));
        assertSame(result.get(3), start.get(4));
    }

    public final void testSeriesDeleteUndershoot() {
        try {
            start.delete(-1);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    public final void testSeriesDeleteOvershoot() {
        try {
            start.delete(5);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

}
