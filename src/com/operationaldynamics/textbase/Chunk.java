/*
 * Text.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.textbase;

class Chunk
{
    final char[] text;

    final int offset;

    final int width;

    /**
     * Build an initial Chunk from a String as a baseline.
     */
    Chunk(String start) {
        this.width = start.length();
        this.text = new char[width];
        start.getChars(0, width, this.text, 0);
        this.offset = 0;
    }

    /**
     * Make a new Chunk out of an existing one.
     */
    Chunk(Chunk existing, int offset, int length) {
        this.text = existing.text;
        this.offset = existing.offset + offset;
        this.width = length;
    }

    /**
     * Get a Java String equal to the array of chars represented by this
     * Chunk.
     */
    public String toString() {
        return new String(text, offset, width);
    }
}
