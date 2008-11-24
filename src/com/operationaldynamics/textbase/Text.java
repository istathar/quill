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

/**
 * A mutable buffer of unicode text to which changes are undoable.
 * 
 * @author Andrew Cowie
 */
/*
 * There are a number of places here that we hunt linearly through the array
 * of Chunks to find offsets. We'll probably need to cache or otherwise build
 * a datastructure around the offset:Chunk relationship.
 */
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
            str.append(chunk.text, chunk.start, chunk.width);
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

    /**
     * Splice a Chunk into the middle of an existing one. You need to have
     * worked out which existing Chunk that is, and what point (offset) into
     * that Chunk you want to do the splice.
     * */
    void spliceInto(Chunk which, int point, Chunk addition) {
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
         * And now copy the remainder of the original array, if any.
         */
        i++;
        if (i != chunks.length) {
            System.arraycopy(chunks, i, next, j, chunks.length - i);
        }

        chunks = next;
    }

    /**
     * Insert a Chunk at the specified array index. This assumes you've worked
     * out the appropriate insertion position in that chunks array!
     */
    void insertAt(int index, Chunk addition) {
        final Chunk[] next;
        int i;

        next = new Chunk[chunks.length + 1];
        i = index;

        /*
         * Copy the first half of the original array, assuming we're not
         * inserting at the beginning
         */
        if (i != 0) {
            System.arraycopy(chunks, 0, next, 0, i);
        }

        next[i] = addition;

        /*
         * And now copy the remainder of the original array, if any.
         */

        if (i != chunks.length) {
            System.arraycopy(chunks, i, next, i + 1, chunks.length - i);
        }

        chunks = next;
    }

    /**
     * Insert the given Java String at the specified offset.
     */
    /*
     * Work out whether the offset falls between two Chunks [meaning we can
     * call insertBetween()], or whether it falls inside a Chunk [implying we
     * must call spliceInto()], with the corner case of it being at the end,
     * in which case we append();
     */
    public void insert(int offset, String what) {
        int start, next;
        Chunk addition;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }
        if (what == null) {
            return;
        }

        start = 0;
        next = 0;
        addition = new Chunk(what);

        for (int i = 0; i < chunks.length; i++) {
            if (start == offset) {
                insertAt(i, addition);
                return;
            }

            next = chunks[i].width;

            if (start + next > offset) {
                spliceInto(chunks[i], offset - start, addition);
                return;
            }

            start += next;
        }

        if (start == offset) {
            append(addition);
            return;
        }

        throw new IndexOutOfBoundsException();
    }
}
