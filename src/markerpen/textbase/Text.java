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
package markerpen.textbase;

/**
 * A mutable buffer of unicode text which manages a linked list of Spans in
 * order to maximize sharing of character array storage.
 * 
 * @author Andrew Cowie
 */
public class Text
{
    Piece first;

    protected Text(String str) {
        first = new Piece();
        first.span = new StringSpan(str, null);
    }

    Text(Span initial) {
        first = new Piece();
        first.span = initial;
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
            result += piece.span.getWidth();
            piece = piece.next;
        }

        return result;
    }

    public String toString() {
        final StringBuilder str;
        Piece piece;

        str = new StringBuilder("");
        piece = first;

        while (piece != null) {
            if (piece.span instanceof CharacterSpan) {
                str.append(piece.span.getChar());
            } else {
                str.append(piece.span.getText());
            }
            piece = piece.next;
        }

        return str.toString();
    }

    void append(Span addition) {
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
        last.span = addition;
    }

    /**
     * Insert the given Java String at the specified offset.
     */
    protected void insert(int offset, String what) {
        insert(offset, new StringSpan(what, null));
    }

    /**
     * Cut a Piece in two at point. Amend the linkages so that overall Text is
     * the same after the operation.
     */
    Piece splitAt(Piece from, int point) {
        Piece preceeding, one, two, following;
        Span before, after;

        /*
         * If it's already a width one span, then we're done. Note that this
         * includes width one StringSpans.
         */

        if (from.span.getWidth() == 1) {
            return from;
        }

        /*
         * Otherwise, split the StringSpan into two StringSpans. FIXME or
         * CharacterSpans?
         */

        before = new StringSpan(from.span, 0, point);
        after = new StringSpan(from.span, point);

        /*
         * and wrap Pieces around them.
         */

        one = new Piece();
        two = new Piece();

        preceeding = from.prev;

        if (preceeding == null) {
            first = one;
        } else {
            preceeding.next = one;
            one.prev = preceeding;
        }

        one.span = before;
        one.next = two;

        two.prev = one;
        two.span = after;

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
     * two Pieces; or null if the offset co
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

            following = start + piece.span.getWidth();

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
    void insert(int offset, Span addition) {
        Piece one, two, piece;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        piece = new Piece();
        piece.span = addition;

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
     * This will operate on the Text, doing a split at each end. That isn't
     * strictly necessary, except that the reason you're usually calling this
     * is to delete, so the boundaries are a good first step, and it makes the
     * algorithm here far simpler.
     * 
     * Returns a Piece wrapping the extracted Chunk (and linked in to the
     * Text).
     */
    Piece concatonateFrom(int offset, int width) {
        final Piece preceeding, two, following, splice;
        Piece p;
        Chunk c;
        int i;
        char[] data;
        byte[] markup;
        final Chunk extract;

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

        two = splitAt(offset + width);
        following = two.next;

        /*
         * Copy characters from the Pieces in the middle into a char[], then
         * form them into a Chunk.
         */

        data = new char[width];
        markup = null;

        if (preceeding == null) {
            if (following == null) {
                p = first;
            } else {
                p = two;
            }
        } else {
            p = preceeding.next;
        }
        i = 0;

        while (p != following) {
            c = p.chunk;

            System.arraycopy(c.text, c.start, data, i, c.width);

            if (c.markup != null) {
                if (markup == null) {
                    markup = new byte[width];
                }
                System.arraycopy(c.markup, c.start, markup, i, c.width);
            }

            i += c.width;
            p = p.next;
        }

        extract = new Chunk(data, markup);

        /*
         * Now embed this extract into a Piece and splice that into the Text.
         * This is the "wasteful" part if we're deleting, except that it gives
         * us the handle we need to locate the boundaries. Think of it as a
         * tuple :)
         */

        splice = new Piece();
        splice.chunk = extract;

        if (preceeding == null) {
            first = splice;
        } else {
            preceeding.next = splice;
            splice.prev = preceeding;
        }

        if (following != null) {
            splice.next = following;
            following.prev = splice;
        }

        return splice;
    }

    /**
     * Delete a width wide segment starting at offset. All the hard work is
     * done by the concatonate method, this just removes the splice. Returns a
     * Chunk representing the removed range.
     */
    protected Chunk delete(int offset, int width) {
        final Piece splice, preceeding, following;
        // final Chunk extract;

        splice = concatonateFrom(offset, width);
        // extract = splice.chunk;

        preceeding = splice.prev;
        following = splice.next;

        /*
         * There are a number of corner cases here, the most notable being
         * what happens if you delete from the beginning (in which case you
         * need to change the first pointer), and the very special case of
         * deleting everything (in which case we put in an "empty" Piece with
         * a zero length Chunk).
         */
        if (offset == 0) {
            if (following == null) {
                first = new Piece();
                first.chunk = new Chunk(splice.chunk, 0, 0);
            } else {
                following.prev = null;
                first = following;
            }
        } else {
            preceeding.next = following;
            if (following != null) {
                following.prev = preceeding;
            }
        }

        return splice.chunk;
    }

    /**
     * If format is negative, the formats it refers to will be removed instead
     * of applied. Like delete() it returns the Chunk representing the splice.
     */
    protected Chunk format(int offset, int width, byte format) {
        final Piece splice;
        Chunk c;
        int i;

        /*
         * We have to go to some trouble to ensure the caller doesn't try to
         * use the most significant bit, since that's the signedness
         * indicator.
         */
        if ((format < 0) && (format < -0x7F)) {
            throw new IllegalArgumentException();
        } else if (format > 0x7F) {
            throw new IllegalArgumentException();
        }

        splice = concatonateFrom(offset, width);

        c = splice.chunk;

        if (c.markup == null) {
            c = new Chunk(c.text, new byte[c.text.length]);
            splice.chunk = c;
        }

        if (format >= 0) {
            for (i = c.start; i < c.start + c.width; i++) {
                c.markup[i] |= format;
            }
        } else {
            format = (byte) -format;
            for (i = c.start; i < c.start + c.width; i++) {
                c.markup[i] &= c.markup[i] ^ format;
            }
        }

        return splice.chunk;
    }
}
