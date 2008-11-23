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

    final int length;

    /**
     * Build an initial Chunk from a String as a baseline.
     */
    Chunk(String start) {
        this.length = start.length();
        this.text = new char[length];
        start.getChars(0, length, this.text, 0);
        this.offset = 0;
    }

    /**
     * Make a new Chunk out of an existing one.
     */
    Chunk(Chunk existing, int offset, int length) {
        this.text = existing.text;
        this.offset = existing.offset + offset;
        this.length = length;
    }

    public String toString() {
        return new String(text, offset, length);
    }
}
