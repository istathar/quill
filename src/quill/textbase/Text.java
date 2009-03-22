/*
 * Text.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * A mutable buffer of unicode text which manages a linked list of Spans in
 * order to maximize sharing of character array storage.
 * 
 * @author Andrew Cowie
 */
public class Text
{
    Piece first;

    protected Text() {
        first = null;
    }

    Text(String str) {
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

        str = new StringBuilder();
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

        /*
         * Handle empty Text case
         */

        if (first == null) {
            first = new Piece();
            first.span = addition;
            return;
        }

        /*
         * Otherwise, we are appending. Hop to the end.
         */

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
     * Insert the given range of Spans at the specified offset.
     */
    protected void insert(int offset, Span[] range) {
        final Piece one, two;
        Piece p, last;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        /*
         * Create the insertion point
         */

        one = splitAt(offset);
        if (one == null) {
            two = first;
        } else {
            two = one.next;
        }

        /*
         * Create and insert Pieces wrapping the Spans.
         */

        last = one;

        for (Span s : range) {
            p = new Piece();
            p.span = s;
            p.prev = last;
            last = p;
        }

        /*
         * And correct the linkages in the reverse direction
         */

        last.next = two;
        if (two != null) {
            two.prev = last;
        }

        p = last;

        do {
            p = p.prev;
            if (p == null) {
                first = last;
                break;
            }
            p.next = last;
            last = p;
        } while (p != one);
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

    public Extract extractAll() {
        Piece p, last;
        Span[] range;

        if (first == null) {
            return null;
        }

        /*
         * Maybe we should cache last?
         */

        p = first;

        while (p.next != null) {
            p = p.next;
        }

        last = p;

        /*
         * get an array
         */

        range = formArray(first, last);
        return new Extract(range);
    }

    /**
     * Delete a width wide segment starting at offset. Returns a Span[]
     * representing the removed range.
     */
    protected void delete(int offset, int width) {
        final Piece preceeding, following;
        final Pair pair;

        /*
         * Ensure splices at the start and end points of the deletion, and
         * find out the Pieces deliniating it. Then create a Span[] of the
         * range that will be removed which we can return out for storage in
         * the Change stack.
         * 
         * TODO now that we have exposed extractRange() and people have to
         * call it before creating a DeleteChange, this will likely be
         * duplicate effort. Once we are caching offsets hopefully we can cut
         * down the work.
         */

        pair = extractFrom(offset, width);

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
                first = null;
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
    }

    /**
     * Add or remove a Markup format from a range of text.
     */
    protected void format(int offset, int width, Markup format) {
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

            p.span = s.applyMarkup(format);

            if (p == pair.two) {
                break;
            }

            p = p.next;
        }
    }

    /**
     * Remove a Markup format from a range of text.
     */
    protected void clear(int offset, int width, Markup format) {
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

            p.span = s.removeMarkup(format);

            if (p == pair.two) {
                break;
            }

            p = p.next;
        }
    }

    /**
     * Clear all markup from a range of text.
     */
    protected void clear(int offset, int width) {
        final Pair pair;
        Piece p;
        Span s;

        pair = extractFrom(offset, width);

        p = pair.one;

        while (true) {
            s = p.span;

            if (s.getMarkup() != null) {
                p.span = s.copy(null);
            }

            if (p == pair.two) {
                break;
            }

            p = p.next;
        }
    }

    /*
     * FIXME This is another case where we linear search through the offsets.
     * We should probably be caching this? Also, this is essentially the same
     * code as splitAt(), so something probably needs to be abstracted out
     * here.
     */
    public Markup getMarkupAt(int offset) {
        Piece piece, last;
        int start, following;

        piece = first;
        last = first;
        start = 0;

        while (piece != null) {
            if (start == offset) {
                return last.span.getMarkup();
            }

            /*
             * Failing that, then let's see if this Piece contains the offset
             * point.
             */

            following = start + piece.span.getWidth();

            if (following > offset) {
                return piece.span.getMarkup();
            }
            start = following;

            last = piece;
            piece = piece.next;
        }

        /*
         * Reached the end
         */

        if (start == offset) {
            if (first == null) {
                return null;
            } else {
                return last.span.getMarkup();
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gets the array of Spans that represent the characters and formatting
     * width wide from start. The result is returned wrapped in a read-only
     * Extract object.
     * 
     * <p>
     * If width is negative, start will be decremented by that amount and the
     * range will be
     * 
     * <pre>
     * extractRange(start-width, |width|)
     * </pre>
     * 
     * This accounts for the common but subtle bug that if you have selected
     * moving backwards, selectionBound will be at a point where the range
     * ends - and greater than insertBound.
     */
    /*
     * Having exposed this so that external APIs can get a Range to pass when
     * constructing a DeleteChange, we probably end up duplicating a lot of
     * work when actually calling delete() after this here.
     */
    public Extract extractRange(int start, int width) {
        final Pair pair;
        final Span[] spans;

        if (width < 0) {
            throw new IllegalArgumentException();
        }

        pair = extractFrom(start, width);
        spans = formArray(pair);
        return new Extract(spans);
    }

    /*
     * Strictly there is no reason for this to be here, but it allows us to
     * keep the constructors in Extract out of view.
     */
    public static Extract extractFor(Span span) {
        return new Extract(span);
    }

    /**
     * Generate an array of Extracts, one for each \n separated paragraph.
     */
    public Extract[] extractLines() {
        Extract[] result;
        Piece p, alpha, omega;
        Span s;
        int num, i;

        if (first == null) {
            return new Extract[] {};
        }

        /*
         * First work out how many lines are in this Text as it stands right
         * now. Assumes that we don't have any StringSpans containing
         * newlines.
         */

        num = 1;
        p = first;

        while (p != null) {
            s = p.span;

            if (s.getChar() == '\n') {
                num++;
            }

            p = p.next;
        }

        result = new Extract[num];

        /*
         * Now gather the Spans together that comprise each paragraph. This
         * relies rather heavily on the assumption that there is not a newline
         * character at the end of the Text.
         */

        i = 0;
        p = first;
        s = null;
        start = 0;
        width = 0;

        alpha = p;
        omega = p;

        while (p != null) {
            s = p.span;

            if (s.getChar() == '\n') {
                if (alpha == p) {
                    /*
                     * blank paragraph
                     */
                    result[i] = new Extract();
                } else {
                    /*
                     * normal paragraph
                     */
                    result[i] = new Extract(formArray(alpha, omega));
                }
                i++;
                alpha = p.next;
            } else {
                omega = p;
            }

            p = p.next;
        }
        if (s.getChar() == '\n') {
            result[i] = new Extract();
        } else {
            result[i] = new Extract(formArray(alpha, omega));
        }

        return result;
    }
}
