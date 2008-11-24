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

    public final void testInitialSplice() {
        final Text text;
        final Chunk initial, addition;

        initial = new Chunk("Cononate");
        addition = new Chunk("cat");

        text = new Text(initial);
        assertEquals("Cononate", text.toString());
        assertEquals(1, text.chunks.length);

        text.splice(initial, 3, addition);
        assertEquals(3, text.chunks.length);
        assertEquals("Concatonate", text.toString());
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
        assertEquals(7, text.chunks.length);

        /*
         * Now, try splicing something in
         */

        addition = new Chunk("wentyT");
        text.splice(two, 1, addition);
        assertEquals("One TwentyTwo Three Four", text.toString());
        assertEquals(9, text.chunks.length);
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
}
