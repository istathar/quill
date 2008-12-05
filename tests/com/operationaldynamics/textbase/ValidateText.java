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
        final Chunk initial;
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

    public final void testSplittingAtPieceBoundary() {
        final Text text;
        final Chunk a, b;
        final Piece first, one, two;

        a = new Chunk("Alpha");
        b = new Chunk("Bravo");

        text = new Text(a);
        text.append(b);
        assertEquals("AlphaBravo", text.toString());

        assertNull(text.first.prev);
        assertNull(text.first.next.next);

        first = text.first;
        one = text.splitAt(5);
        assertEquals("AlphaBravo", text.toString());

        assertSame(first, one);
        assertNotNull(one.next);
        assertNotSame(one, one.next);
        assertEquals("Alpha", one.chunk.toString());

        two = one.next;
        assertEquals("Bravo", two.chunk.toString());
        assertNull(two.next);
        assertEquals(one, two.prev);
    }

    public final void testSingleSplice() {
        final Text text;

        text = new Text("This Emergency Broadcast System");

        text.insert(5, "is a test of the ");
        assertEquals("This is a test of the Emergency Broadcast System", text.toString());
    }

    public final void testMultipleSplice() {
        final Text text;
        final Chunk one, two, three, four, space, addition;
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

        assertEquals(7, calculateNumberPieces(text));

        /*
         * Now, try splicing something in
         */

        addition = new Chunk("wentyT");
        text.insert(5, addition);
        assertEquals("One TwentyTwo Three Four", text.toString());

        assertEquals(9, calculateNumberPieces(text));
    }

    private static int calculateNumberPieces(Text text) {
        Piece p;
        int i;

        i = 0;
        p = text.first;
        while (p != null) {
            p = p.next;
            i++;
        }
        return i;
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

        text.insert(0, zero);
        assertEquals("ZeroOneTwoThree", text.toString());

        text.insert(15, zero);
        assertEquals("ZeroOneTwoThreeZero", text.toString());

        text.insert(7, zero);
        assertEquals("ZeroOneZeroTwoThreeZero", text.toString());
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
        final Piece splice;

        zero = new Chunk("Zero");
        one = new Chunk("One");
        two = new Chunk("Two");
        three = new Chunk("Three");

        text = new Text(zero);
        text.append(one);
        text.append(two);
        text.append(three);

        assertEquals("ZeroOneTwoThree", text.toString());

        splice = text.concatonateFrom(2, 11);

        assertEquals("ZeroOneTwoThree", text.toString());
        result = splice.chunk;
        assertEquals(0, result.start);
        assertEquals(11, result.width);
        assertEquals("roOneTwoThr", result.toString());
    }

    public final void testConcatonateAll() {
        final Text text;
        final Chunk zero, one, two, result;
        final Piece splice;

        zero = new Chunk("James");
        one = new Chunk(" T. ");
        two = new Chunk("Kirk");

        text = new Text(zero);
        text.append(one);
        text.append(two);

        assertEquals("James T. Kirk", text.toString());

        splice = text.concatonateFrom(0, 13);

        assertEquals("James T. Kirk", text.toString());
        result = splice.chunk;
        assertEquals(0, result.start);
        assertEquals(13, result.width);
        assertEquals("James T. Kirk", result.toString());
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
        assertEquals(2, calculateNumberPieces(text));
        assertEquals("Ze", text.first.chunk.toString());
        assertEquals("ee", text.first.next.chunk.toString());

        text.delete(1, 2);
        assertEquals("Ze", text.toString());
        assertEquals(2, calculateNumberPieces(text));
    }

    public final void testDeleteBoundaries() {
        final Text text;

        text = new Text("Hello World");

        text.delete(0, 6);
        assertEquals("World", text.toString());
        assertEquals(1, calculateNumberPieces(text));

        text.delete(3, 2);
        assertEquals("Wor", text.toString());
        assertEquals(1, calculateNumberPieces(text));
    }

    public final void testDeleteAll() {
        final Text text;

        text = new Text("Magic");

        text.delete(0, 5);
        assertEquals("", text.toString());
        assertEquals(1, calculateNumberPieces(text));
    }

    public final void testBoundsChecking() {
        final Text text;

        text = new Text("Magic");
        try {
            text.insert(6, "ian");
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, calculateNumberPieces(text));

        try {
            text.delete(7, 3);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, calculateNumberPieces(text));

        try {
            text.delete(2, 6);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(2, calculateNumberPieces(text));
        // is ok
    }

    private static byte currentFormat(Piece p) {
        if (p.chunk.markup == null) {
            return 0;
        }
        return p.chunk.markup[0];
    }

    public void testApplyFormatting() {
        final Text text;
        Piece p;
        final byte[] target;

        text = new Text("Hello World");

        /*
         * Call format() on the first word. This will splice; the first Piece
         * will have the format, the second will not.
         */

        text.format(0, 5, (byte) 0x0A);

        assertEquals(2, calculateNumberPieces(text));
        p = text.first;
        assertEquals(0x0A, currentFormat(p));
        p = p.next;
        assertEquals(0x00, currentFormat(p));
        assertEquals("Hello World", text.toString());

        /*
         * Format second word
         */

        text.format(6, 5, (byte) 0x01);

        assertEquals(3, calculateNumberPieces(text));
        p = text.first;
        assertEquals(0x0A, currentFormat(p));
        p = p.next;
        assertEquals(0x00, currentFormat(p));
        assertEquals(" ", p.chunk.toString());
        p = p.next;
        assertEquals(0x01, currentFormat(p));
        assertEquals("World", p.chunk.toString());
        assertNull(p.next);
        assertEquals("Hello World", text.toString());

        /*
         * Now do something across entire text
         */

        text.format(0, 11, (byte) 0x04);

        assertEquals(1, calculateNumberPieces(text));
        target = new byte[] {
                0x0E, 0x0E, 0x0E, 0x0E, 0x0E, 0x04, 0x05, 0x05, 0x05, 0x05, 0x05
        };
        for (int i = 0; i < 11; i++) {
            assertEquals(target[i], text.first.chunk.markup[i]);
        }
        assertEquals("Hello World", text.toString());
    }

    public void testRemovingBits() {
        assertEquals(0xf000, (0xfff0 & 0xf000));
        assertEquals(0xfff0, (0x0ff0 | 0xf000));
        assertEquals(0xf0f0, (0xfff0 & 0xf0f0));
        assertEquals(0xf0f0, (0xfff0 & (0xfff0 ^ 0x0f00)));
        assertEquals(0xfff0, (0xfff0 & (0xfff0 ^ 0x0000)));
    }

    public void testRemovingFormatting() {
        final char[] data;
        final byte[] markup, target;
        final Chunk chunk;
        final Text text;

        data = new char[] {
                'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'
        };
        markup = new byte[] {
                0x0E, 0x0E, 0x0E, 0x0E, 0x0E, 0x04, 0x05, 0x07, 0x07, 0x02, 0x07
        };
        target = new byte[] {
                0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x04, 0x05, 0x05, 0x05, 0x00, 0x05
        };

        chunk = new Chunk(data, markup);
        text = new Text(chunk);
        assertEquals("Hello World", text.toString());

        text.format(0, 11, (byte) -0x02);

        for (int i = 0; i < 11; i++) {
            assertEquals(target[i], text.first.chunk.markup[i]);
        }
    }

    public void testNullShortcut() {
        final char[] data;
        final Chunk chunk;
        final Text text;

        data = new char[] {
                'H', 'i', '?'
        };

        chunk = new Chunk(data, null);
        text = new Text(chunk);
        assertEquals("Hi?", text.toString());

        text.format(0, 3, (byte) 0x02);

        for (int i = 0; i < 3; i++) {
            assertEquals(0x02, text.first.chunk.markup[i]);
        }

        text.format(0, 3, (byte) -0x02);

        for (int i = 0; i < 3; i++) {
            assertEquals(0x00, text.first.chunk.markup[i]);
        }
    }

    public void testRemovingFormatFromZero() {
        final char[] data;
        final Chunk chunk;
        final Text text;

        data = new char[] {
                'Y', 'o', '!'
        };

        chunk = new Chunk(data, null);
        text = new Text(chunk);
        assertEquals("Yo!", text.toString());

        /*
         * Should have no effect
         */

        text.format(0, 3, (byte) -0x02);

        for (int i = 0; i < 3; i++) {
            assertEquals(0x00, text.first.chunk.markup[i]);
        }

        /*
         * Should again have no effect
         */

        text.format(0, 3, (byte) -0x0F);

        for (int i = 0; i < 3; i++) {
            assertEquals(0x00, text.first.chunk.markup[i]);
        }
    }

    public final void testAllowedFormatBits() {
        final Text text;

        text = new Text("Arnold");

        text.format(2, 3, (byte) 0x7F);
        text.format(2, 3, (byte) -0x7F);

        try {
            text.format(2, 3, (byte) 0x80);
            fail();
        } catch (IllegalArgumentException iae) {
            // good
        }

        try {
            text.format(2, 3, (byte) -0x80);
            fail();
        } catch (IllegalArgumentException iae) {
            // good
        }

        text.format(1, 4, (byte) (1 << 6));
        try {
            text.format(0, 5, (byte) (1 << 7));
            fail();
        } catch (IllegalArgumentException iae) {
            // good
        }
    }
}
