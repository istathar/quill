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
public class TextChain
{
    Piece first;

    /**
     * cache of the length of this TextChain, in characters.
     */
    private int length = -1;

    public TextChain() {
        first = null;
    }

    TextChain(String str) {
        first = new Piece();
        first.span = Span.createSpan(str, null);
    }

    TextChain(Span initial) {
        first = new Piece();
        first.span = initial;
    }

    private void invalidateCache() {
        length = -1;
    }

    /**
     * Update the offset cache [stored in the Pieces] in the process of
     * calculating and storing the length of the TextChain.
     */
    private void calculateOffsets() {
        Piece piece;
        int result;

        piece = first;
        result = 0;

        while (piece != null) {
            piece.offset = result;
            result += piece.span.getWidth();
            piece = piece.next;
        }

        length = result;
    }

    /**
     * The length of this Text, in characters.
     */
    public int length() {
        if (length == -1) {
            calculateOffsets();
        }
        return length;
    }

    public String toString() {
        final StringBuilder str;
        Piece piece;

        str = new StringBuilder();
        piece = first;

        while (piece != null) {
            str.append(piece.span.getText());
            piece = piece.next;
        }

        return str.toString();
    }

    public void append(Span addition) {
        Piece piece;
        final Piece last;

        if (addition == null) {
            throw new IllegalArgumentException();
        }
        invalidateCache();

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
        insert(offset, Span.createSpan(what, null));
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
        invalidateCache();

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

        if (point == 0) {
            return from;
        }

        /*
         * Otherwise, split the StringSpan into two Spans - StringSpans
         * ordinarily, but CharacterSpans if the widths are down to 1. When
         * called on two succeeding offsets that happen to enclose a newline,
         * this gives us newlines isolated in their own CharacterSpans.
         */

        before = from.span.split(0, point);
        after = from.span.split(point);

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
     * Find the Piece enclosing offset. This is used to then subdivide by
     * splitAt()
     */
    /*
     * TODO implementation is an ugly linear search. Now that the offsets are
     * cached in the Pieces, perhaps we can be smarter about hunting.
     */
    Piece pieceAt(final int offset) {
        Piece piece, last;
        int start;

        if (offset == 0) {
            return first;
        }

        if (length == -1) {
            throw new IllegalStateException("\n"
                    + "You must to ensure offsets are calculated before calling this");
        }

        if (offset == length) {
            return null;
        }

        if (offset > length) {
            throw new IndexOutOfBoundsException();
        }

        piece = first;
        last = first;
        start = 0;

        while (piece != null) {
            start = piece.offset;

            if (start > offset) {
                return last;
            }
            if (start == offset) {
                return piece;
            }

            last = piece;
            piece = piece.next;
        }

        return last;
    }

    /**
     * Find the Piece containing offset, and split it into two. Handle the
     * boundary cases of an offset at a Piece boundary. Returns a Pair around
     * the two Pieces. null will be set if there is no Piece before (or after)
     * this point.
     */
    /*
     * FIXME this code was copied to pieceAt(), and should call that method
     * instead of doing the same work here.
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
        invalidateCache();

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
        invalidateCache();

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
     * Delete a width wide segment starting at offset.
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

        invalidateCache();

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

    public Markup getMarkupAt(int offset) {
        final Piece piece;

        if (length == -1) {
            calculateOffsets();
        }

        piece = pieceAt(offset);
        if (piece == null) {
            return null;
        } else {
            return piece.span.getMarkup();
        }
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
     * Having exposed this so that external APIs can get an Extract to pass
     * when constructing a DeleteChange, we probably end up duplicating a lot
     * of work when actually calling delete() after this here.
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
    public Extract[] extractParagraphs() {
        Extract[] result;
        Piece p, alpha, omega;
        Span s;
        int num, len, i, delta;

        if (first == null) {
            return new Extract[] {};
        }

        /*
         * First work out how many lines are in this Text as it stands right
         * now.
         */

        num = 1;
        p = first;

        while (p != null) {
            s = p.span;
            len = s.getWidth();

            delta = -1;
            for (i = 0; i < len; i++) {
                if (s.getChar(i) == '\n') {
                    delta = i;
                    break;
                }
            }

            if (delta == 0) {
                p = splitAt(p, 1);
                num++;
            } else if (delta > 0) {
                p = splitAt(p, delta);
                p = p.next;
                p = splitAt(p, 1);
                num++;
            }
            p = p.next;
        }

        result = new Extract[num];

        /*
         * This relies rather heavily on the assumption that there is not a
         * newline character at the end of the TextChain.
         */

        i = 0;
        p = first;
        s = null;
        alpha = p;
        omega = p;

        while (p != null) {
            s = p.span;

            if (s.getChar(0) == '\n') { // FIXME use of 0
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
        if (s.getChar(0) == '\n') {
            result[i] = new Extract();
        } else {
            result[i] = new Extract(formArray(alpha, omega));
        }

        return result;
    }

    private Segment belongs;

    /**
     * Tell this TextChain what Segment it belongs to
     */
    void setEnclosingSegment(Segment segment) {
        this.belongs = segment;
    }

    /**
     * Get the Segment that this TextChain is backing
     */
    Segment getEnclosingSegment() {
        return belongs;
    }

    /**
     * Given a cursor location in offset, work backwards to find a word
     * boundary, and then forwards to the next word boundary, and return the
     * word contained between those two points.
     */
    public String getWordAt(final int offset) {
        final Piece origin, alpha, omega;
        int start, end, len;
        Piece p;
        int i;
        boolean found;
        Pair pair;
        StringBuilder str;
        char ch; // FIXME int

        if (length == -1) {
            calculateOffsets();
        }

        if (offset == length) {
            return null;
        }

        origin = pieceAt(offset);

        i = offset - origin.offset;
        ch = (char) origin.span.getChar(i);
        if (!Character.isLetter(ch)) {
            return null;
        }

        /*
         * Calculate start by seeking backwards to find whitespace
         */

        p = origin;
        start = offset;
        i = -1;
        found = false;

        while (p != null) {
            i = start - p.offset;

            while (i > 0) {
                i--;
                ch = (char) p.span.getChar(i);
                if (!Character.isLetter(ch)) {
                    if (ch != '\'') {
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                break;
            }

            start = p.offset;
            p = p.prev;
        }

        if (!found) {
            start = 0;
        } else {
            start = p.offset + i + 1;
        }

        /*
         * Calculate end by seeking forwards to find whitespace
         */

        p = origin;
        end = offset;
        i = end - p.offset;
        found = false;

        while (p != null) {
            len = p.span.getWidth();

            while (i < len) {
                ch = (char) p.span.getChar(i);
                if (!Character.isLetter(ch)) {
                    if (ch != '\'') {
                        found = true;
                        break;
                    }
                }
                i++;
            }

            if (found) {
                break;
            }

            p = p.next;
            i = 0;
        }

        if (p == null) {
            end = length;
        } else {
            end = p.offset + i;
        }

        /*
         * Now pull out the word
         */

        pair = extractFrom(start, end - start);
        alpha = pair.one;
        omega = pair.two;

        /*
         * FIXME this is BAD! We need to return a Span? Or maybe concatonate
         * in order to return a new Span's getText()?
         */

        str = new StringBuilder();

        p = alpha;
        while (p != null) {
            str.append(p.span.getText());
            if (p == omega) {
                break;
            }
            p = p.next;
        }

        return str.toString();
    }
}
