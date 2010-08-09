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

public class ValidateStructuralChange extends TestCase
{
    private Segment[] segments;

    public void setUp() {
        TextChain chain;

        segments = new Segment[] {
                new ComponentSegment(), new HeadingSegment(), new NormalSegment(),
        };

        chain = new TextChain("Chapter 1");
        segments[0].setText(chain);

        chain = new TextChain("The beginning");
        segments[1].setText(chain);

        chain = new TextChain("Hello World");
        segments[2].setText(chain);
    }

    public final void testSetup() {
        final Series series;

        series = new Series(segments);
        assertEquals(3, series.size());
        assertEquals("Hello World", series.get(2).getText().toString());
    }

    /**
     * We don't really want this as a toString() on Series, so we just put it
     * here as a utility method.
     */
    private static String concatonate(Series series) {
        final StringBuilder str;
        Segment segment;
        int i;
        TextChain chain;

        str = new StringBuilder();

        for (i = 0; i < series.size(); i++) {
            segment = series.get(i);
            chain = segment.getText();
            str.append(chain.toString());
        }

        return str.toString();
    }

    public final void testSpliceSameSegmentMiddle() {
        final Series before, after;
        final Segment inserted;
        TextChain chain;
        final StructuralChange change;

        before = new Series(segments);
        assertEquals(3, before.size());

        inserted = new PreformatSegment();
        chain = new TextChain("cruel() ");
        inserted.setText(chain);

        change = new SplitStructuralChange(before, segments[2], 6, inserted);
        change.apply();

        after = change.getAfter();

        assertEquals(5, after.size());
        assertSame(after.get(0), segments[0]);
        assertSame(after.get(1), segments[1]);
        assertSame(after.get(2), segments[2]);
        assertTrue(after.get(3) instanceof PreformatSegment);
        assertTrue(after.get(4) instanceof NormalSegment);
        assertEquals("Chapter 1The beginningHello cruel() World", concatonate(after));
    }

    public final void testRevertStructuralChange() {
        final Series series, one, two;
        final Segment inserted;
        TextChain chain;
        final StructuralChange change;

        series = new Series(segments);
        assertEquals(3, series.size());
        assertEquals("Chapter 1The beginningHello World", concatonate(series));

        inserted = new PreformatSegment();
        chain = new TextChain("cruel() ");
        inserted.setText(chain);

        change = new SplitStructuralChange(series, segments[2], 6, inserted);
        change.apply();
        one = change.getAfter();
        assertNotSame(series, one);
        assertEquals("Chapter 1The beginningHello cruel() World", concatonate(one));

        change.undo();
        two = change.getBefore();
        assertSame(series, two);

        assertEquals("Chapter 1The beginningHello World", concatonate(two));
    }

    public final void testSpliceSameSegmentEnd() {
        final Series before, middle, after;
        final Segment inserted;
        TextChain chain;
        final StructuralChange change;

        before = new Series(segments);
        assertEquals(3, before.size());

        inserted = new PreformatSegment();
        chain = new TextChain("save()");
        inserted.setText(chain);

        change = new SplitStructuralChange(before, segments[2], 11, inserted);
        change.apply();

        middle = change.getAfter();

        assertEquals(4, middle.size());
        assertSame(middle.get(0), segments[0]);
        assertSame(middle.get(1), segments[1]);
        assertSame(middle.get(2), segments[2]);
        assertTrue(middle.get(3) instanceof PreformatSegment);
        assertEquals("Chapter 1The beginningHello Worldsave()", concatonate(middle));

        change.undo();

        // IMPROVE
        after = change.getBefore();
        assertEquals("Chapter 1The beginningHello World", concatonate(after));
    }

    public final void testSpliceSameSegmentBegin() {
        Series series;
        final Segment inserted;
        final TextChain chain;
        final StructuralChange change;

        series = new Series(segments);
        assertEquals(3, series.size());

        inserted = new PreformatSegment();
        chain = new TextChain("init()");
        inserted.setText(chain);

        change = new SplitStructuralChange(series, segments[0], 0, inserted);
        change.apply();

        series = change.getAfter();
        assertEquals(4, series.size());
        assertTrue(series.get(0) instanceof PreformatSegment);
        assertSame(series.get(1), segments[0]);
        assertSame(series.get(2), segments[1]);
        assertSame(series.get(3), segments[2]);

        assertEquals("init()Chapter 1The beginningHello World", concatonate(series));

        change.undo();

        // IMPROVE
        series = change.getBefore();
        assertEquals("Chapter 1The beginningHello World", concatonate(series));
    }
}
