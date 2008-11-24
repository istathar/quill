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

import java.util.Arrays;

public class Text
{
    Chunk[] chunks;

    public Text(String str) {
        chunks = new Chunk[1];
        chunks[0] = new Chunk(str);
    }

    Text(Chunk initial) {
        chunks = new Chunk[1];
        chunks[0] = initial;
    }

    /**
     * The length of this Text, in characters.
     */
    /*
     * Should we be caching this?
     */
    public int length() {
        int result;

        result = 0;

        for (Chunk chunk : chunks) {
            result += chunk.width;
        }

        return result;
    }

    public String toString() {
        final StringBuilder str;

        str = new StringBuilder();

        for (Chunk chunk : chunks) {
            str.append(chunk.text, chunk.offset, chunk.width);
        }

        return str.toString();
    }

    void append(Chunk addition) {
        final Chunk[] next;
        final int i;

        if (addition == null) {
            throw new IllegalArgumentException();
        }

        i = chunks.length;
        next = Arrays.copyOf(chunks, i + 1);
        next[i] = addition;

        chunks = next;
    }

    void splice(Chunk which, int point, Chunk addition) {
        final Chunk before, after;
        final Chunk[] next;
        int i, j;

        before = new Chunk(which, 0, point);
        after = new Chunk(which, point, which.width - point);

        next = new Chunk[chunks.length + 2];

        /*
         * Identify the index which corresponds to. Yes, a List might be
         * better here.
         */

        for (i = 0; i < chunks.length; i++) {
            if (chunks[i] == which) {
                break;
            }
        }

        if (i != 0) {
            System.arraycopy(chunks, 0, next, 0, i);
        }

        /*
         * Now replace which with before, addition, after
         */
        j = i;

        next[j++] = before;
        next[j++] = addition;
        next[j++] = after;

        /*
         * And not copy the remainder of the original array, if any.
         */
        i++;
        if (i != chunks.length) {
            System.arraycopy(chunks, i, next, j, chunks.length - i);
        }

        chunks = next;
    }
}
