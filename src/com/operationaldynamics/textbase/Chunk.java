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

/**
 * A segment of text. This is essentially a reimplementation of Java's String
 * class, with an emphasis on immutability and reusability; once allocated the
 * char[] can be reused by new Chunks as they are created to be subsets of an
 * existing one. This is common in cut and paste operations, but also in the
 * case of the initial load of a file, where the bulk of the datastore is
 * frequently broken by splices, but for which there is no reason to
 * reallocate. This, in turn, forms the basis of the undo ability in Text.
 * 
 * @author Andrew Cowie
 */
class Chunk
{
    final char[] text;

    /**
     * The offset into the char[] where this Chunk begins.
     */
    final int start;

    /**
     * The length of the segment of the char[] that this Chunk represents,
     * from offset.
     */
    final int width;

    /**
     * Build an initial Chunk from a String as a baseline.
     */
    Chunk(String str) {
        this.width = str.length();
        this.text = new char[width];
        str.getChars(0, width, this.text, 0);
        this.start = 0;
    }

    /**
     * Make a new Chunk out of an existing one.
     */
    Chunk(Chunk existing, int offset, int length) {
        if ((offset + length) > existing.width) {
            throw new IllegalArgumentException();
        }
        this.text = existing.text;
        this.start = existing.start + offset;
        this.width = length;
    }

    /**
     * Make a new Chunk from the supplied character array. Do not change any
     * elements of the char[] after passing it in.
     */
    /*
     * Java has no mechanism to prevent this, so if you keep a reference to
     * data and then torque it, you will break everything. This is obviously
     * an encapsulation violation. There doesn't seem any way to avoid this
     * other than making a copy here, which would be needless since the only
     * thing calling this is here in this package.
     */
    Chunk(char[] data) {
        this.width = data.length;
        this.text = data;
        this.start = 0;
    }

    /**
     * Get a Java String equal to the array of chars represented by this
     * Chunk.
     */
    public String toString() {
        return new String(text, start, width);
    }
}
