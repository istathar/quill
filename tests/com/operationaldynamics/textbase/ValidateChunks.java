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

public class ValidateChunks extends TestCase
{
    public final void testInitialChunk() {
        final Chunk initial;

        initial = new Chunk("Hello world");

        assertEquals("Hello world", new String(initial.text));
    }

    public final void testIntialChunkToString() {
        final Chunk initial;

        initial = new Chunk("Hello world");

        assertEquals("Hello world", initial.toString());
    }

    public final void testChunkFromExsiting() {
        final Chunk initial, subsequent, another, third, fourth;

        initial = new Chunk("Emergency broadcast system");

        subsequent = new Chunk(initial, 10, 9);
        assertEquals("broadcast", subsequent.toString());
        assertSame(initial.text, subsequent.text);
        assertEquals(9, subsequent.width);
        assertEquals(10, subsequent.start);

        another = new Chunk(initial, 0, 9);
        assertEquals("Emergency", another.toString());

        third = new Chunk(initial, 20, 6);
        assertEquals("system", third.toString());

        fourth = new Chunk(third, 0, 3);
        assertSame(initial.text, third.text);
        assertSame(fourth.text, third.text);
        assertEquals(20, fourth.start);
        assertEquals(3, fourth.width);
        assertEquals("sys", fourth.toString());
    }
}
