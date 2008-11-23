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
}
