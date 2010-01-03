/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.util.ArrayList;

import junit.framework.TestCase;

import static quill.textbase.Span.createSpan;

public class ValidateText extends TestCase
{
    public final void testInitialText() {
        final TextChain start;

        start = new TextChain("Hello world");
        assertEquals(11, start.length());

        assertEquals("Hello world", start.toString());
    }

    public final void testTwoSequentialChunks() {
        final TextChain text;
        final Span second;

        text = new TextChain("Hello world");

        second = createSpan(" it is a sunny day", null);
        text.append(second);

        assertEquals("Hello world it is a sunny day".length(), text.length());

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

        chain = new TextChain();

        assertEquals(0, chain.length());
        assertEquals("", chain.toString());
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

    public final void testCheckSampleData() {
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

    public final void testSpanAtEnd() {
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

    public final void testSplittingAtPoint() {
        final Node node, one, two;
        final Span initial;

        initial = createSpan("Concave", null);
        node = Node.createNode(initial);
        assertEquals("Concave", convertToString(node));

        one = node.subset(0, 3);
        assertEquals("Concave", convertToString(node));
        assertEquals("Con", convertToString(one));

        two = node.subset(3, 4);
        assertEquals("Concave", convertToString(node));
        assertEquals("cave", convertToString(two));
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

        assertEquals(7, countNumberOfSpans(text));

        /*
         * Now, try splicing something in
         */

        addition = createSpan("wentyT", null);
        text.insert(5, addition);
        assertEquals("One TwentyTwo Three Four", text.toString());

        assertEquals(9, countNumberOfSpans(text));
    }

    private static int countNumberOfSpans(TextChain chain) {
        final Node root;
        final CountingSpanVisitor tourist;

        root = chain.getTree();

        if (root == null) {
            return 0;
        }

        tourist = new CountingSpanVisitor();
        root.visit(tourist);
        return tourist.count;
    }

    private static class CountingSpanVisitor implements SpanVisitor
    {
        private int count;

        private CountingSpanVisitor() {
            count = 0;
        }

        public boolean visit(Span span) {
            count++;
            return false;
        }
    }

    /**
     * Local utility to turn a tree into an array. This is obviously
     * inefficient, but a large number of the original Extract tests here
     * relied on this behaviour (since Extract wraps Span[]) so we wrap
     * tree<Span> in Span[] to get on with it.
     */
    private static Span[] convertToSpanArray(final Node node) {
        final AccumulatingSpanVisitor tourist;

        if (node == null) {
            return new Span[] {};
        }

        tourist = new AccumulatingSpanVisitor();
        node.visit(tourist);
        return tourist.getList();
    }

    private static class AccumulatingSpanVisitor implements SpanVisitor
    {
        private ArrayList<Span> list;

        private AccumulatingSpanVisitor() {
            list = new ArrayList<Span>(8);
        }

        public boolean visit(Span span) {
            list.add(span);
            return false;
        }

        private Span[] getList() {
            final Span[] result;

            result = new Span[list.size()];
            list.toArray(result);

            return result;
        }
    }

    /**
     * Test utility to turn a tree into a String.
     */
    private static String convertToString(final Node node) {
        final StringSpanVisitor tourist;

        if (node == null) {
            return "";
        }

        tourist = new StringSpanVisitor();
        node.visit(tourist);
        return tourist.getString();
    }

    private static class StringSpanVisitor implements SpanVisitor
    {
        private StringBuilder str;

        private StringSpanVisitor() {
            str = new StringBuilder();
        }

        public boolean visit(Span span) {
            str.append(span.getText());
            return false;
        }

        private String getString() {
            return str.toString();
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

    public final void testSubsetNothing() {
        final TextChain text;
        final Node tree;
        Node node;

        text = new TextChain("All good people");
        tree = text.getTree();

        node = tree.subset(2, 0);
        assertSame(node, Node.EMPTY);
        node = tree.subset(0, 0);
        assertSame(node, Node.EMPTY);
        node = tree.subset(15, 0);
        assertSame(node, Node.EMPTY);
    }

    public final void testSubsetRange() {
        final TextChain text;
        final Span zero, one, two, three;
        final Span[] range;
        final Node tree, node;

        zero = createSpan("Zero", null);
        one = createSpan("One", null);
        two = createSpan("Two", null);
        three = createSpan("Three", null);

        text = new TextChain(zero);
        text.append(one);
        text.append(two);
        text.append(three);

        assertEquals("ZeroOneTwoThree", text.toString());

        tree = text.getTree();
        node = tree.subset(2, 11);
        assertEquals("ZeroOneTwoThree", text.toString());

        range = convertToSpanArray(node);
        assertEquals(11, lengthOf(range));
        assertEquals("roOneTwoThr", textOf(range));
    }

    public final void testSubsetAll() {
        final TextChain text;
        final Span zero, one, two;
        final Node tree, node;
        final Span[] range;

        zero = createSpan("James", null);
        one = createSpan(" T. ", null);
        two = createSpan("Kirk", null);

        text = new TextChain(zero);
        text.append(one);
        text.append(two);

        assertEquals("James T. Kirk", text.toString());

        tree = text.getTree();
        node = tree.subset(0, 13);

        assertEquals("James T. Kirk", text.toString());

        range = convertToSpanArray(node);
        assertEquals(13, lengthOf(range));
        assertEquals("James T. Kirk", textOf(range));
    }

    public final void testDeleteRange() {
        final TextChain text;
        final Span zero, one, two, three;
        Node node;
        Span[] outcome;

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
        assertEquals(2, countNumberOfSpans(text));

        node = text.getTree();
        outcome = convertToSpanArray(node);
        assertEquals("Ze", outcome[0].getText());
        assertEquals("ee", outcome[1].getText());

        text.delete(1, 2);
        assertEquals("Ze", text.toString());
        assertEquals(2, countNumberOfSpans(text));
    }

    public final void testDeleteBoundaries() {
        final TextChain text;

        text = new TextChain("Hello World");

        text.delete(0, 6);
        assertEquals("World", text.toString());
        assertEquals(1, countNumberOfSpans(text));

        text.delete(3, 2);
        assertEquals("Wor", text.toString());
        assertEquals(1, countNumberOfSpans(text));
    }

    public final void testDeleteAll() {
        final TextChain text;

        text = new TextChain("Magic");

        text.delete(0, 5);
        assertEquals("", text.toString());
        assertEquals(0, countNumberOfSpans(text));
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
        assertEquals(1, countNumberOfSpans(text));

        try {
            text.delete(7, 3);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, countNumberOfSpans(text));

        try {
            text.delete(2, 6);
            fail();
        } catch (IndexOutOfBoundsException ioobe) {
            // good
        }

        assertEquals("Magic", text.toString());
        assertEquals(1, countNumberOfSpans(text));
        // is ok
    }

    public final void testApplyFormatting() {
        final TextChain text;
        Node tree;
        Span[] results;

        text = new TextChain("Hello World");

        /*
         * Call format() on the first word. This will splice; the first Piece
         * will have the format, the second will not.
         */

        text.format(0, 5, Common.ITALICS);

        assertEquals(2, countNumberOfSpans(text));
        tree = text.getTree();
        results = convertToSpanArray(tree);
        assertSame(results[0].getMarkup(), Common.ITALICS);
        assertSame(results[1].getMarkup(), null);

        assertEquals("Hello World", text.toString());

        /*
         * Format second word
         */

        text.format(6, 5, Common.BOLD);

        assertEquals(3, countNumberOfSpans(text));
        tree = text.getTree();
        results = convertToSpanArray(tree);
        assertSame(results[0].getMarkup(), Common.ITALICS);
        assertSame(results[1].getMarkup(), null);
        assertEquals(" ", results[1].getText());

        assertSame(results[2].getMarkup(), Common.BOLD);
        assertEquals("World", results[2].getText());

        assertEquals("Hello World", text.toString());

        /*
         * Now do something across entire text
         */

        text.format(0, 11, Common.FILENAME);

        // NEW: will replace all
        assertEquals(1, countNumberOfSpans(text));
        tree = text.getTree();
        results = convertToSpanArray(tree);
        assertSame(results[0].getMarkup(), Common.FILENAME);

        assertEquals("Hello World", text.toString());
    }

    public final void testRemovingBits() {
        assertEquals(0xf000, (0xfff0 & 0xf000));
        assertEquals(0xfff0, (0x0ff0 | 0xf000));
        assertEquals(0xf0f0, (0xfff0 & 0xf0f0));
        assertEquals(0xf0f0, (0xfff0 & (0xfff0 ^ 0x0f00)));
        assertEquals(0xfff0, (0xfff0 & (0xfff0 ^ 0x0000)));
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
