/*
 * ValidateText.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package markerpen.textbase;

import junit.framework.TestCase;

import static markerpen.textbase.Text.formArray;

public class ValidateText extends TestCase
{
    public final void testInitialText() {
        final Text start;

        start = new Text("Hello world");

        assertEquals("Hello world", start.toString());
    }

    public final void testTwoSequentialChunks() {
        final Text text;
        final Span second;

        text = new Text("Hello world");

        second = new StringSpan(" it is a sunny day", null);
        text.append(second);

        assertEquals("Hello world it is a sunny day", text.toString());
    }

    public final void testExtractedChunks() {
        final Text text;
        final Span one, two, three, space;

        one = new StringSpan("Emergency", null);
        two = new StringSpan("broadcast", null);
        three = new StringSpan("system", null);
        space = new CharacterSpan(' ', null);

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
        final Span initial;
        final Piece one, two;

        initial = new StringSpan("Concave", null);
        text = new Text(initial);
        assertEquals("Concave", text.toString());
        assertNull(text.first.next);
        assertNull(text.first.prev);

        one = text.splitAt(text.first, 3);
        assertEquals("Concave", text.toString());

        assertNotNull(one.next);
        assertEquals("Con", one.span.getText());

        two = one.next;

        assertEquals("cave", two.span.getText());
        assertNull(two.next);
        assertEquals(one, two.prev);
    }

    public final void testSplittingAtPieceBoundary() {
        final Text text;
        final Span a, b;
        final Piece first, one, two;

        a = new StringSpan("Alpha", null);
        b = new StringSpan("Bravo", null);

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
        assertEquals("Alpha", one.span.getText());

        two = one.next;
        assertEquals("Bravo", two.span.getText());
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
        final Span one, two, three, four, space, addition;
        one = new StringSpan("One", null);
        space = new CharacterSpan(' ', null);
        two = new StringSpan("Two", null);
        three = new StringSpan("Three", null);
        four = new StringSpan("Four", null);

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

        addition = new StringSpan("wentyT", null);
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
        final Span zero, one, two;

        zero = new StringSpan("Hello", null);
        assertEquals(5, zero.getWidth());
        assertEquals(5, zero.getText().length());
        text = new Text(zero);
        assertEquals(5, text.length());

        one = new StringSpan("Happy", null);
        two = new StringSpan("Days", null);

        text.append(one);
        text.append(two);
        assertEquals(14, text.length());
    }

    public final void testInsertBetweenExistingChunks() {
        final Text text;
        final Span zero, one, two, three;

        zero = new StringSpan("Zero", null);
        one = new StringSpan("One", null);
        two = new StringSpan("Two", null);
        three = new StringSpan("Three", null);

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

    private static int lengthOf(Span[] range) {
        int i;

        i = 0;
        for (Span s : range) {
            i += s.getWidth();
        }

        return i;
    }

    private static String textOf(Span[] range) {
        final StringBuilder str;

        str = new StringBuilder();
        for (Span s : range) {
            str.append(s.getText());
        }

        return str.toString();
    }

    public final void testExtractRange() {
        final Text text;
        final Span zero, one, two, three;
        final Span[] range;
        final Pair pair;

        zero = new StringSpan("Zero", null);
        one = new StringSpan("One", null);
        two = new StringSpan("Two", null);
        three = new StringSpan("Three", null);

        text = new Text(zero);
        text.append(one);
        text.append(two);
        text.append(three);

        assertEquals("ZeroOneTwoThree", text.toString());

        pair = text.extractFrom(2, 11);

        assertEquals("ZeroOneTwoThree", text.toString());

        range = formArray(pair);
        assertEquals(11, lengthOf(range));
        assertEquals("roOneTwoThr", textOf(range));
    }

    public final void testExtractAll() {
        final Text text;
        final Span zero, one, two;
        final Pair pair;
        final Span[] range;

        zero = new StringSpan("James", null);
        one = new StringSpan(" T. ", null);
        two = new StringSpan("Kirk", null);

        text = new Text(zero);
        text.append(one);
        text.append(two);

        assertEquals("James T. Kirk", text.toString());

        pair = text.extractFrom(0, 13);

        assertEquals("James T. Kirk", text.toString());

        range = formArray(pair);
        assertEquals(13, lengthOf(range));
        assertEquals("James T. Kirk", textOf(range));
    }

    public final void testDeleteRange() {
        final Text text;
        final Span zero, one, two, three;

        zero = new StringSpan("Zero", null);
        one = new StringSpan("One", null);
        two = new StringSpan("Two", null);
        three = new StringSpan("Three", null);

        text = new Text(zero);
        text.append(one);
        text.append(two);
        text.append(three);
        assertEquals("ZeroOneTwoThree", text.toString());

        text.delete(2, 11);
        assertEquals("Zeee", text.toString());
        assertEquals(2, calculateNumberPieces(text));
        assertEquals("Ze", text.first.span.getText());
        assertEquals("ee", text.first.next.span.getText());

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

    private final boolean containsFormat(Piece p, Markup format) {
        if (p.span.markup == null) {
            return false;
        }

        for (Markup m : p.span.markup) {
            if (m == format) {
                return true;
            }
        }
        return false;
    }

    public final void testApplyFormatting() {
        final Text text;
        Piece p;

        text = new Text("Hello World");

        /*
         * Call format() on the first word. This will splice; the first Piece
         * will have the format, the second will not.
         */

        text.format(0, 5, Common.ITALICS, true);

        assertEquals(2, calculateNumberPieces(text));
        p = text.first;
        assertTrue(containsFormat(p, Common.ITALICS));
        p = p.next;
        assertNull(p.span.markup);
        assertFalse(containsFormat(p, Common.ITALICS));
        assertEquals("Hello World", text.toString());

        /*
         * Format second word
         */

        text.format(6, 5, Common.BOLD, true);

        assertEquals(3, calculateNumberPieces(text));
        p = text.first;
        assertTrue(containsFormat(p, Common.ITALICS));
        p = p.next;
        assertEquals(null, p.span.markup);
        assertEquals(" ", p.span.getText());
        p = p.next;
        assertTrue(containsFormat(p, Common.BOLD));
        assertEquals("World", p.span.getText());
        assertNull(p.next);
        assertEquals("Hello World", text.toString());

        /*
         * Now do something across entire text
         */

        text.format(0, 11, Common.FILENAME, true);

        assertEquals(3, calculateNumberPieces(text));
        p = text.first;
        assertTrue(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertTrue(containsFormat(p, Common.BOLD));

        assertEquals("Hello World", text.toString());
    }

    public final void testRemovingBits() {
        assertEquals(0xf000, (0xfff0 & 0xf000));
        assertEquals(0xfff0, (0x0ff0 | 0xf000));
        assertEquals(0xf0f0, (0xfff0 & 0xf0f0));
        assertEquals(0xf0f0, (0xfff0 & (0xfff0 ^ 0x0f00)));
        assertEquals(0xfff0, (0xfff0 & (0xfff0 ^ 0x0000)));
    }

    public final void testRemovingFormatting() {
        final Text text;
        Piece p;

        text = new Text("Hello World");
        assertEquals("Hello World", text.toString());

        /*
         * Setup as demonstrated above
         */

        text.format(0, 5, Common.ITALICS, true);
        text.format(6, 5, Common.BOLD, true);
        text.format(0, 11, Common.FILENAME, true);

        /*
         * Now remove one, and test
         */

        text.format(0, 11, Common.ITALICS, false);

        assertEquals(3, calculateNumberPieces(text));
        p = text.first;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertTrue(containsFormat(p, Common.BOLD));

        /*
         * Does doing it twice hurt?
         */

        text.format(0, 11, Common.ITALICS, false);

        assertEquals(3, calculateNumberPieces(text));
        p = text.first;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertTrue(containsFormat(p, Common.BOLD));

        text.format(3, 5, Common.FILENAME, false);

        assertEquals(5, calculateNumberPieces(text));

        // Hel
        p = text.first;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));

        // "lo"
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertFalse(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));

        // " "
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertFalse(containsFormat(p, Common.FILENAME));
        assertFalse(containsFormat(p, Common.BOLD));

        // "Wo"
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertFalse(containsFormat(p, Common.FILENAME));
        assertTrue(containsFormat(p, Common.BOLD));

        // "rld"
        p = p.next;
        assertFalse(containsFormat(p, Common.ITALICS));
        assertTrue(containsFormat(p, Common.FILENAME));
        assertTrue(containsFormat(p, Common.BOLD));
    }

    public final void testRemovingFormatFromZero() {
        final Text text;
        Piece p;

        text = new Text("Yo!");
        assertEquals("Yo!", text.toString());

        p = text.first;
        while (p != null) {
            assertNull(p.span.markup);
            p = p.next;
        }

        /*
         * Should have no effect
         */

        text.format(0, 3, Common.FILENAME, false);

        p = text.first;
        while (p != null) {
            assertNull(p.span.markup);
            p = p.next;
        }

        /*
         * Should again have no effect
         */

        text.format(0, 3, Common.FILENAME, false);

        p = text.first;
        while (p != null) {
            assertNull(p.span.markup);
            p = p.next;
        }
    }
}
