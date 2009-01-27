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

    // TODO
    protected void insert(int offset, Span[] range) {
        assert false;
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
     * boundary cases of an offset at a Piece boundary. Returns a Pair around
     * the two Pieces. null will be set if there is no Piece before (or after)
     * this point.
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
     * Get an array of Pieces representing the concatonation of the marked up
     * Spans in a given range.
     * 
     * Allocate a new Chunk by concatonating any and all Chunks starting at
     * offset for width. While deleting, as such, does not require this,
     * undo/redo does.
     * 
     * This will operate on the Text, doing a split at each end. That isn't
     * strictly necessary, except that the reason you're usually calling this
     * is to delete, so the boundaries are a good first step, and it makes the
     * algorithm here far simpler.
     * 
     * Returns a Pair of the two Pieces enclosing the extracted range.
     */
    Pair extractFrom(int offset, int width) {
        final Piece preceeding, alpha, omega;

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
         * Ensure splices at the start and end points of the deletion, and
         * find out the pieces deliniating it. Then create a Span[] of the
         * range that will be removed which we can return out for storage in
         * the Change stack.
         */

        preceeding = splitAt(offset);
        omega = splitAt(offset + width);

        if (preceeding == null) {
            if (omega.prev == null) {
                alpha = omega;
            } else {
                alpha = first;
            }
        } else {
            alpha = preceeding.next;
        }

        return new Pair(alpha, omega);
    }

    static Span[] formArray(Pair pair) {
        return formArray(pair.one, pair.two);
    }

    /**
     * Form a Span[] from the Spans between Pieces start and end.
     */
    static Span[] formArray(Piece alpha, Piece omega) {
        final Span[] result;
        Piece p;
        int i;

        /*
         * Need to know how many pieces are in between so we can size the
         * return array.
         */

        p = alpha;
        i = 1;

        while (p != omega) {
            i++;
            p = p.next;
        }

        result = new Span[i];

        /*
         * And now populate the array representing the concatonation.
         */

        p = alpha;

        for (i = 0; i < result.length; i++) {
            result[i] = p.span;
            p = p.next;
        }

        return result;
    }

    /**
     * Delete a width wide segment starting at offset. Returns a Span[]
     * representing the removed range.
     */
    protected Span[] delete(int offset, int width) {
        final Piece preceeding, following;
        final Span[] result;
        final Pair pair;

        /*
         * Ensure splices at the start and end points of the deletion, and
         * find out the Pieces deliniating it. Then create a Span[] of the
         * range that will be removed which we can return out for storage in
         * the Change stack.
         */

        pair = extractFrom(offset, width);

        result = formArray(pair);

        /*
         * Now change the linkages so we affect the deletion. There are a
         * number of corner cases here, the most notable being what happens if
         * you delete from the beginning (in which case you need to change the
         * first pointer), and the very special case of deleting everything
         * (in which case we put in an "empty" Piece with a zero length Span).
         */

        preceeding = pair.one.prev;
        following = pair.two.next;

        if (offset == 0) {
            if (following == null) {
                first = new Piece();
                first.span = new StringSpan("", null); // TODO?
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

        return result;
    }

    /**
     * Add or remove a Markup format from a range of text. Like delete() it
     * returns the Span[] representing the splice.
     */
    protected Span[] format(int offset, int width, Markup format, boolean additive) {
        final Pair pair;
        Piece p;
        Span s;

        pair = extractFrom(offset, width);

        /*
         * Unlike the insert() and delete() operations, we can leave the Piece
         * sequence alone. The difference is that we change the Spans that are
         * pointed to in the range being changed.
         */

        p = pair.one;

        while (true) {
            s = p.span;

            if (additive) {
                p.span = s.applyMarkup(format);
            } else {
                p.span = s.removeMarkup(format);
            }

            if (p == pair.two) {
                break;
            }

            p = p.next;
        }

        return formArray(pair);
    }
}
