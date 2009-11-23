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

package quill.textbase;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import junit.framework.TestCase;

import static quill.textbase.Span.createSpan;
import static quill.textbase.TextChain.formArray;

public class ValidateText extends TestCase
{
    public final void testInitialText() {
        final TextChain start;

        start = new TextChain("Hello world");

        assertEquals("Hello world", start.toString());
    }

    public final void testTwoSequentialChunks() {
        final TextChain text;
        final Span second;

        text = new TextChain("Hello world");

        second = createSpan(" it is a sunny day", null);
        text.append(second);

        assertEquals("Hello world it is a sunny day", text.toString());
    }

    public final void testExtractedChunks() {
        final TextChain text;
        final Span one, two, three, space;

        one = createSpan("Emergency", null);
        two = createSpan("broadcast", null);
        three = createSpan("system", null);
        space = createSpan(' ', null);

        text = new TextChain();
        text.append(three);
        text.append(space);
        text.append(two);
        text.append(space);
        text.append(one);

        assertEquals("system broadcast Emergency", text.toString());
    }

    public final void testEmptyChain() {
        final TextChain chain;
        Span span;

        chain = new TextChain();

        span = chain.spanAt(0);
        assertNull(span);
    }

    private static TextChain sampleData() {
        final TextChain result;

        result = new TextChain();
        result.append(Span.createSpan("One", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Two", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Three", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Four", null));

        return result;
    }

    public final void testCheckSamplePieces() {
        final String expected;
        final TextChain chain;

        expected = "One Two Three Four";
        chain = sampleData();

        assertEquals(18, expected.length());
        assertEquals(18, chain.length());
        assertEquals(expected, chain.toString());
    }

    public final void testSpanAt() {
        final TextChain chain;
        Span span;

        chain = sampleData();

        span = chain.spanAt(0);
        assertEquals("One", span.getText());
        span = chain.spanAt(1);
        assertEquals("One", span.getText());
        span = chain.spanAt(2);
        assertEquals("One", span.getText());
        span = chain.spanAt(3);
        assertEquals(" ", span.getText());
        span = chain.spanAt(4);
        assertEquals("Two", span.getText());

        span = chain.spanAt(12);
        assertEquals("Three", span.getText());
    }

    public final void testPieceAtEnd() {
        final TextChain chain;
        Span span;

        chain = sampleData();

        span = chain.spanAt(17);
        assertEquals("Four", span.getText());

        span = chain.spanAt(18);
        assertNull(span);

        try {
            span = chain.spanAt(19);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }
    }

    // FIXME
    public final void skipSplittingAtPoint() {
        final TextChain text;
        final Span initial;

        initial = createSpan("Concave", null);
        text = new TextChain(initial);
        assertEquals("Concave", text.toString());

        one = text.splitAt(text.first, 3);
        assertEquals("Concave", text.toString());

        assertNotNull(one.next);
        assertEquals("Con", one.span.getText());

        two = one.next;

        assertEquals("cave", two.span.getText());
        assertNull(two.next);
        assertEquals(one, two.prev);
    }

    // FIXME
    public final void skipSplittingAtPieceBoundary() {
        final TextChain text;
        final Span a, b;
        final Piece first, one, two;

        a = createSpan("Alpha", null);
        b = createSpan("Bravo", null);

        text = new TextChain(a);
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
        final TextChain text;

        text = new TextChain("This Emergency Broadcast System");

        text.insert(5, "is a test of the ");
        assertEquals("This is a test of the Emergency Broadcast System", text.toString());
    }

    public final void testMultipleSplice() {
        final TextChain text;
        final Span one, two, three, four, space, addition;
        one = createSpan("One", null);
        space = createSpan(' ', null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);
        four = createSpan("Four", null);

        text = new TextChain(one);
        text.append(space);
        text.append(two);
        text.append(space);
        text.append(three);
        text.append(space);
        text.append(four);

        assertEquals("One Two Three Four", text.toString());

        assertEquals(7, introspectNumberOfSpans(text));

        /*
         * Now, try splicing something in
         */

        addition = createSpan("wentyT", null);
        text.insert(5, addition);
        assertEquals("One TwentyTwo Three Four", text.toString());

        assertEquals(9, introspectNumberOfSpans(text));
    }

    private static int introspectNumberOfSpans(TextChain chain) {
        final Field field;
        Object spans;
        Span s;
        final int result;

        try {
            field = TextChain.class.getDeclaredField("spans");
            field.setAccessible(true);
            spans = field.get(chain);
            result = Array.getLength(spans);

            return result;

        } catch (Exception e) {
            fail(e.toString());
            return -1;
        }
    }

    public final void testTextLength() {
        final TextChain text;
        final Span zero, one, two;

        zero = createSpan("Hello", null);
        assertEquals(5, zero.getWidth());
        assertEquals(5, zero.getText().length());
        text = new TextChain(zero);
        assertEquals(5, text.length());

        one = createSpan("Happy", null);
        two = createSpan("Days", null);

        text.append(one);
        text.append(two);
        assertEquals(14, text.length());
    }

    public final void testInsertBeginning() {
        final TextChain text;
        final Span zero, one, two, three;

        zero = createSpan("Zero", null);
        one = createSpan("One", null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);

        text = new TextChain();
        text.append(one);
        text.append(two);
        text.append(three);

        assertEquals("OneTwoThree", text.toString());
        assertEquals(11, text.length());

        text.insert(0, zero);
        assertEquals("ZeroOneTwoThree", text.toString());
        assertEquals(15, text.length());
    }

    public final void testInsertBetweenExistingSpans() {
        final TextChain text;
        final Span zero, one, two, three;

        zero = createSpan("Zero", null);
        one = createSpan("One", null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);

        text = new TextChain(zero);
        text.append(two);
        text.append(three);

        assertEquals("ZeroTwoThree", text.toString());
        assertEquals(12, text.length());

        text.insert(4, one);
        assertEquals("ZeroOneTwoThree", text.toString());
        assertEquals(15, text.length());

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
        final TextChain text;
        final String str;

        str = "All this has happened before";
        text = new TextChain(str);

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

    public final void testExtractNothing() {
        final TextChain text;
        Pair pair;

        text = new TextChain("All good people");

        pair = text.extractFrom(2, 0);
        assertNull(pair);
        pair = text.extractFrom(0, 0);
        assertNull(pair);
        pair = text.extractFrom(15, 0);
        assertNull(pair);
    }

    public final void testExtractRange() {
        final TextChain text;
        final Span zero, one, two, three;
        final Span[] range;
        final Pair pair;

        zero = createSpan("Zero", null);
        one = createSpan("One", null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);

        text = new TextChain(zero);
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
        final TextChain text;
        final Span zero, one, two;
        final Pair pair;
        final Span[] range;

        zero = createSpan("James", null);
        one = createSpan(" T. ", null);
        two = createSpan("Kirk", null);

        text = new TextChain(zero);
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
        final TextChain text;
        final Span zero, one, two, three;

        zero = createSpan("Zero", null);
        one = createSpan("One", null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);

        text = new TextChain(zero);
        text.append(one);
        text.append(two);
        text.append(three);
        assertEquals("ZeroOneTwoThree", text.toString());

        text.delete(2, 11);
        assertEquals("Zeee", text.toString());
        assertEquals(2, introspectNumberOfSpans(text));
        assertEquals("Ze", text.first.span.getText());
        assertEquals("ee", text.first.next.span.getText());

        text.delete(1, 2);
        assertEquals("Ze", text.toString());
        assertEquals(2, introspectNumberOfSpans(text));
    }

    public final void testDeleteBoundaries() {
        final TextChain text;

        text = new TextChain("Hello World");

        text.delete(0, 6);
        assertEquals("World", text.toString());
        assertEquals(1, introspectNumberOfSpans(text));

        text.delete(3, 2);
        assertEquals("Wor", text.toString());
        assertEquals(1, introspectNumberOfSpans(text));
    }

    public final void testDeleteAll() {
        final TextChain text;

        text = new TextChain("Magic");

        text.delete(0, 5);
        assertEquals("", text.toString());
        assertEquals(0, introspectNumberOfSpans(text));
    }

    public final void testBoundsChecking() {
        final TextChain text;

        text = new TextChain("Magic");
        try {
            text.insert(6, "ian");
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, introspectNumberOfSpans(text));

        try {
            text.delete(7, 3);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, introspectNumberOfSpans(text));

        try {
            text.delete(2, 6);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(2, introspectNumberOfSpans(text));
        // is ok
    }

    public final void testApplyFormatting() {
        final TextChain text;
        Piece p;

        text = new TextChain("Hello World");

        /*
         * Call format() on the first word. This will splice; the first Piece
         * will have the format, the second will not.
         */

        text.format(0, 5, Common.ITALICS);

        assertEquals(2, introspectNumberOfSpans(text));
        p = text.first;
        assertSame(p.span.getMarkup(), Common.ITALICS);
        p = p.next;
        assertNull(p.span.getMarkup());
        assertEquals("Hello World", text.toString());

        /*
         * Format second word
         */

        text.format(6, 5, Common.BOLD);

        assertEquals(3, introspectNumberOfSpans(text));
        p = text.first;
        assertSame(p.span.getMarkup(), Common.ITALICS);
        p = p.next;
        assertEquals(null, p.span.getMarkup());
        assertEquals(" ", p.span.getText());
        p = p.next;
        assertSame(p.span.getMarkup(), Common.BOLD);
        assertEquals("World", p.span.getText());
        assertNull(p.next);
        assertEquals("Hello World", text.toString());

        /*
         * Now do something across entire text
         */

        text.format(0, 11, Common.FILENAME);

        // NEW: will replace all
        assertEquals(3, introspectNumberOfSpans(text));
        p = text.first;
        assertSame(p.span.getMarkup(), Common.FILENAME);
        p = p.next;
        assertSame(p.span.getMarkup(), Common.FILENAME);
        p = p.next;
        assertSame(p.span.getMarkup(), Common.FILENAME);
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
        final TextChain text;
        Piece p;

        text = new TextChain("Hello World");
        assertEquals("Hello World", text.toString());

        /*
         * Setup as demonstrated above
         */

        text.format(0, 11, Common.FILENAME);
        text.format(0, 5, Common.ITALICS);
        text.format(6, 5, Common.BOLD);

        /*
         * Now remove one, and test
         */

        text.clear(0, 11, Common.ITALICS);

        assertEquals(3, introspectNumberOfSpans(text));
        p = text.first;
        assertEquals(null, p.span.getMarkup());
        p = p.next;
        assertSame(p.span.getMarkup(), Common.FILENAME);
        p = p.next;
        assertSame(p.span.getMarkup(), Common.BOLD);

        /*
         * Does doing it twice hurt?
         */

        text.clear(0, 11, Common.ITALICS);

        assertEquals(3, introspectNumberOfSpans(text));
        p = text.first;
        assertEquals(null, p.span.getMarkup());
        p = p.next;
        assertSame(p.span.getMarkup(), Common.FILENAME);
        p = p.next;
        assertSame(p.span.getMarkup(), Common.BOLD);

        text.clear(3, 5, Common.FILENAME);

        assertEquals(5, introspectNumberOfSpans(text));

        // Hel
        p = text.first;
        assertEquals(null, p.span.getMarkup());

        // "lo"
        p = p.next;
        assertEquals(null, p.span.getMarkup());

        // " "
        p = p.next;
        assertEquals(null, p.span.getMarkup());

        // "Wo"
        p = p.next;
        assertSame(p.span.getMarkup(), Common.BOLD);

        // "rld"
        p = p.next;
        assertSame(p.span.getMarkup(), Common.BOLD);
    }

    public final void testRemovingFormatFromZero() {
        final TextChain text;
        Piece p;

        text = new TextChain("Yo!");
        assertEquals("Yo!", text.toString());

        p = text.first;
        while (p != null) {
            assertNull(p.span.getMarkup());
            p = p.next;
        }

        /*
         * Should have no effect
         */

        text.clear(0, 3, Common.FILENAME);

        p = text.first;
        while (p != null) {
            assertNull(p.span.getMarkup());
            p = p.next;
        }

        /*
         * Should again have no effect
         */

        text.clear(0, 3, Common.FILENAME);

        p = text.first;
        while (p != null) {
            assertNull(p.span.getMarkup());
            p = p.next;
        }
    }

    public final void testGetMarkupFromChain() {
        final TextChain text;

        text = new TextChain("Hello Wor");
        text.append(createSpan("ld", null));
        text.format(0, 11, Common.FILENAME);
        text.format(0, 5, Common.ITALICS);
        text.format(6, 5, Common.BOLD);

        assertSame(Common.ITALICS, text.getMarkupAt(0));
        assertSame(Common.ITALICS, text.getMarkupAt(1));
        assertSame(Common.ITALICS, text.getMarkupAt(2));
        assertSame(Common.ITALICS, text.getMarkupAt(3));
        assertSame(Common.ITALICS, text.getMarkupAt(4));
        assertSame(Common.FILENAME, text.getMarkupAt(5));
        assertSame(Common.BOLD, text.getMarkupAt(6));
        assertSame(Common.BOLD, text.getMarkupAt(7));
        assertSame(Common.BOLD, text.getMarkupAt(8));
        assertSame(Common.BOLD, text.getMarkupAt(9));
        assertSame(Common.BOLD, text.getMarkupAt(10));

        // Hello_World
        // 000000000011
        // 012345678901

        assertEquals(11, text.length());
        try {
            text.getMarkupAt(11);
        } catch (IllegalArgumentException iae) {
            // good
        }

        text.append(createSpan(" Goodbye", null));

        // _Goodbye
        // 111111111
        // 123456789

        assertSame(null, text.getMarkupAt(11));
        assertSame(null, text.getMarkupAt(12));
        assertSame(null, text.getMarkupAt(13));
        assertSame(null, text.getMarkupAt(14));
        assertSame(null, text.getMarkupAt(15));
        assertSame(null, text.getMarkupAt(16));
        assertSame(null, text.getMarkupAt(17));
        assertSame(null, text.getMarkupAt(18));

        assertEquals(19, text.length());
        try {
            text.getMarkupAt(19);
        } catch (IllegalArgumentException iae) {
            // good
        }
    }
}
