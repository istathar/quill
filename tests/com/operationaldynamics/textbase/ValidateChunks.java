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
}
