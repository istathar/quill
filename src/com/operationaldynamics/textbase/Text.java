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
 * A mutable buffer of unicode text to which changes are undoable.
 * 
 * @author Andrew Cowie
 */
public class Text
{
    Piece first;

    public Text(String str) {
        first = new Piece();
        first.chunk = new Chunk(str);
    }

    Text(Chunk initial) {
        first = new Piece();
        first.chunk = initial;
    }

    /**
     * The length of this Text, in characters.
     */
    /*
     * TODO cache this when we cache the offsets!
     */
    public int length() {
        Piece piece;
        int result;

        piece = first;
        result = 0;

        while (piece != null) {
            result += piece.chunk.width;
            piece = piece.next;
        }

        return result;
    }

    public String toString() {
        final StringBuilder str;
        Piece piece;

        str = new StringBuilder();
        piece = first;

        while (piece != null) {
            str.append(piece.chunk.text, piece.chunk.start, piece.chunk.width);
            piece = piece.next;
        }

        return str.toString();
    }

    void append(Chunk addition) {
        Piece piece;
        final Piece last;

        if (addition == null) {
            throw new IllegalArgumentException();
        }

        piece = first;

        while (piece.next != null) {
            piece = piece.next;
        }

        last = new Piece();
        last.prev = piece;
        piece.next = last;
        last.chunk = addition;
    }

    /**
     * Insert the given Java String at the specified offset.
     */
    public void insert(int offset, String what) {
        insert(offset, new Chunk(what));
    }

    /**
     * Cut a Piece in two at point. Amend the linkages so that overall Text is
     * the same after the operation.
     */
    Piece splitAt(Piece from, int point) {
        Piece preceeding, one, two, following;
        Chunk before, after;

        before = new Chunk(from.chunk, 0, point);
        after = new Chunk(from.chunk, point, from.chunk.width - point);

        one = new Piece();
        two = new Piece();

        preceeding = from.prev;

        if (preceeding == null) {
            first = one;
        } else {
            preceeding.next = one;
            one.prev = preceeding;
        }

        one.chunk = before;
        one.next = two;

        two.prev = one;
        two.chunk = after;

        following = from.next;

        if (following != null) {
            two.next = following;
            following.prev = two;
        }

        return one;
    }

    /**
     * Find the Piece containing offset, and split it into two. Handle the
     * boundary cases of an offset at a Piece boundary. Returns first of the
     * two Pieces.
     */
    /*
     * TODO Initial implementation of this is an ugly linear search; replace
     * this with an offset cache in the Pieces.
     */
    Piece splitAt(int offset) {
        Piece piece, last;
        int start, following;

        if (offset == 0) {
            return null;
        }

        piece = first;
        last = first;

        start = 0;

        while (piece != null) {
            /*
             * Are we already at a Piece boundary?
             */

            if (start == offset) {
                return last;
            }

            /*
             * Failing that, then let's see if this Piece contains the offset
             * point. If it does, figure out the delta into this Piece's Chunk
             * and split at that point.
             */

            following = start + piece.chunk.width;

            if (following > offset) {
                return splitAt(piece, offset - start);
            }
            start = following;

            last = piece;
            piece = piece.next;
        }

        /*
         * Reached the end; so long as there is nothing left we're in an
         * append situation and no problem, otherwise out of bounds.
         */

        if (start == offset) {
            return last;
        }

        throw new IndexOutOfBoundsException();
    }

    /**
     * Splice a Chunk into the Text. The result of doing this is three Pieces;
     * a new Piece before and after, and a Piece wrapping the Chunk and linked
     * between them. This is the workhorse of this class.
     */
    public void insert(int offset, Chunk addition) {
        Piece one, two, piece;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        piece = new Piece();
        piece.chunk = addition;

        if (offset == 0) {
            piece.next = first;
            first.prev = piece;
            first = piece;
            return;
        }

        one = splitAt(offset);
        two = one.next;

        if (two != null) {
            piece.next = two;
            two.prev = piece;
        }
        one.next = piece;
        piece.prev = one;
    }

    /**
     * Allocate a new Chunk by concatonating any and all Chunks starting at
     * offset for width. While deleting, as such, does not require this,
     * undo/redo does.
     * 
     * This will operate on the Text, doing a splits at each end. That isn't
     * strictly necessary, except that the reason you're usually calling this
     * is to delete, so the boundaries are a good first step, and it makes the
     * algorithm here far simpler.
     */
    Chunk concatonateFrom(int offset, int width) {
        final Piece preceeding, one, two, following;
        Piece p;
        Chunk c;
        int i;
        char[] data;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }
        if (width < 0) {
            throw new IllegalArgumentException();
        }

        /*
         * TODO guard the other end, ie test for conditions
         * IndexOutOfBoundsException("offset too high") and
         * IndexOutOfBoundsException("width greater than available text")
         */

        /*
         * Find the Piece containing the start point of the range we want to
         * isolate.
         */

        preceeding = splitAt(offset);
        one = preceeding.next;

        two = splitAt(offset + width);
        following = two.next;

        /*
         * Copy characters from the Pieces in the middle into a char[]
         */

        data = new char[width];
        p = one;
        i = 0;

        while (p != following) {
            c = p.chunk;

            System.arraycopy(c.text, c.start, data, i, c.width);

            i += c.width;
            p = p.next;
        }

        return new Chunk(data);
    }

    /**
     * Delete a width wide segment starting at offset.
     */
    public void delete(int offset, int width) {
        final Chunk before, after;
        final Chunk[] next;
        int i, j, start, following, sum, deltaI, deltaJ;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }
        if (width < 0) {
            throw new IllegalArgumentException();
        }

        if (chunks.length == 1) {
            before = new Chunk(chunks[0], 0, offset);
            after = new Chunk(chunks[0], offset + width, chunks[0].width - (offset + width));

            next = new Chunk[2];
            next[0] = before;
            next[1] = after;

            chunks = next;
            return;
        }

        /*
         * Identify the index of the Chunk containing offset
         */

        start = 0;
        following = 0;
        deltaI = 0;

        for (i = 0; i < chunks.length; i++) {
            if (start == offset) {
                deltaI = 0;
                break;
            }

            following = chunks[i].width;

            if (start + following > offset) {
                deltaI = offset - start;
                break;
            }

            start += following;
        }

        /*
         * Cross over the Chunks that will be dropped
         */

        sum = chunks[i].width - deltaI;
        deltaJ = 0;

        for (j = i + 1; j < chunks.length; j++) {
            sum += chunks[j].width;

            if (sum > width) {
                deltaJ = sum - width;
                break;
            }
        }

        before = new Chunk(chunks[i], 0, deltaI);
        after = new Chunk(chunks[j], deltaJ, chunks[j].width - deltaJ);

        next = new Chunk[i + (chunks.length - j) + 1];

        if (i != 0) {
            System.arraycopy(chunks, 0, next, 0, i);
        }

        /*
         * Now replace
         */

        next[i++] = before;
        next[i++] = after;

        /*
         * And now copy the remainder of the original array, if any.
         */
        j++;
        if (j != chunks.length) {
            System.arraycopy(chunks, j, next, i, chunks.length - j);
        }

        chunks = next;
    }
}
