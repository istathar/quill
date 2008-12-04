/*
 * ValidateChunks.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package com.operationaldynamics.textbase;

import junit.framework.TestCase;

public class ValidateText extends TestCase
{
    public final void testInitialText() {
        final Text start;

        start = new Text("Hello world");

        assertEquals("Hello world", start.toString());
    }

    public final void testTwoSequentialChunks() {
        final Text text;
        final Chunk second;

        text = new Text("Hello world");

        second = new Chunk(" it is a sunny day");
        text.append(second);

        assertEquals("Hello world it is a sunny day", text.toString());
    }

    public final void testExtractedChunks() {
        final Text text;
        final Chunk initial, one, two, three, space;

        initial = new Chunk("Emergency broadcast system");

        one = new Chunk(initial, 0, 9);
        two = new Chunk(initial, 10, 9);
        three = new Chunk(initial, 20, 6);
        space = new Chunk(initial, 9, 1);

        text = new Text("");
        text.append(three);
        text.append(space);
        text.append(two);
        text.append(space);
        text.append(one);

        assertEquals("system broadcast Emergency", text.toString());
    }

    public final void testSplittingAtPoint() {
        final Text text;
        final Chunk initial, addition;
        final Piece one, two;

        initial = new Chunk("Concave");
        text = new Text(initial);
        assertEquals("Concave", text.toString());
        assertNull(text.first.next);
        assertNull(text.first.prev);

        one = text.splitAt(text.first, 3);
        assertEquals("Concave", text.toString());

        assertNotNull(one.next);
        assertEquals("Con", one.chunk.toString());

        two = one.next;
        assertEquals("cave", two.chunk.toString());
        assertNull(two.next);
        assertEquals(one, two.prev);
    }

    public final void testMultipleSplice() {
        final Text text;
        final Chunk one, two, three, four, space, addition;
        Piece p;
        int i;

        one = new Chunk("One");
        space = new Chunk(" ");
        two = new Chunk("Two");
        three = new Chunk("Three");
        four = new Chunk("Four");

        text = new Text(one);
        text.append(space);
        text.append(two);
        text.append(space);
        text.append(three);
        text.append(space);
        text.append(four);

        assertEquals("One Two Three Four", text.toString());

        i = 0;
        p = text.first;
        while (p != null) {
            p = p.next;
            i++;
        }
        assertEquals(7, i);

        /*
         * Now, try splicing something in
         */

        addition = new Chunk("wentyT");
        text.insert(5, addition);
        assertEquals("One TwentyTwo Three Four", text.toString());

        i = 0;
        p = text.first;
        while (p != null) {
            p = p.next;
            i++;
        }
        assertEquals(9, i);
    }

    public final void testTextLength() {
        final Text text;
        final Chunk zero, one, two;

        zero = new Chunk("Hello");
        assertEquals(5, zero.width);
        assertEquals(5, zero.text.length);
        text = new Text(zero);
        assertEquals(5, text.length());

        one = new Chunk("Happy");
        two = new Chunk("Days");

        text.append(one);
        text.append(two);
        assertEquals(14, text.length());
    }

    public final void testInsertBetweenExistingChunks() {
        final Text text;
        final Chunk zero, one, two, three;

        zero = new Chunk("Zero");
        one = new Chunk("One");
        two = new Chunk("Two");
        three = new Chunk("Three");

        text = new Text(one);
        text.append(two);
        text.append(three);

        assertEquals("OneTwoThree", text.toString());

        text.insertAt(0, zero);
        assertEquals("ZeroOneTwoThree", text.toString());

        text.insertAt(4, zero);
        assertEquals("ZeroOneTwoThreeZero", text.toString());

        text.insertAt(2, zero);
        assertEquals("ZeroOneZeroTwoThreeZero", text.toString());
    }

    public final void testInsertIntoAChunkFormingSplice() {
        final Text text;

        text = new Text("This Emergency Broadcast System");

        text.insert(5, "is a test of the ");
        assertEquals("This is a test of the Emergency Broadcast System", text.toString());
    }

    /*
     * Ideally you'd just call append() here, but perhaps you don't know
     * you're at the end. So long as you specify an offset equalling the
     * character length of the Text (ie, not greater), it (should) still still
     * work, which this tests.
     */
    public final void testInsertIntoEnd() {
        final Text text;
        final String str;

        str = "All this has happened before";
        text = new Text(str);

        text.insert(str.length(), ", all this will happen again.");
        assertEquals("All this has happened before, all this will happen again.", text.toString());
    }

    public final void testConcatonatingChunks() {
        final Text text;
        final Chunk zero, one, two, three, result;

        zero = new Chunk("Zero");
        one = new Chunk("One");
        two = new Chunk("Two");
        three = new Chunk("Three");

        text = new Text(zero);
        text.append(one);
        text.append(two);
        text.append(three);

        assertEquals("ZeroOneTwoThree", text.toString());

        result = text.concatonateFrom(2, 11);
        assertEquals(0, result.start);
        assertEquals(11, result.width);
        assertEquals("roOneTwoThr", result.toString());
    }

    public final void testDeleteRange() {
        final Text text;
        final Chunk zero, one, two, three;

        zero = new Chunk("Zero");
        one = new Chunk("One");
        two = new Chunk("Two");
        three = new Chunk("Three");

        text = new Text(zero);
        text.append(one);
        text.append(two);
        text.append(three);
        assertEquals("ZeroOneTwoThree", text.toString());

        text.delete(2, 11);
        assertEquals("Zeee", text.toString());
        assertEquals(2, text.chunks.length);
        assertEquals("Ze", text.chunks[0].toString());
        assertEquals("ee", text.chunks[1].toString());

        text.delete(1, 2);
        assertEquals("Ze", text.toString());
    }
}
