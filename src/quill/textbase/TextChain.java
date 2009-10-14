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

import static java.lang.System.arraycopy;

/**
 * A mutable buffer of unicode text which manages a linked list of Spans in
 * order to maximize sharing of character array storage.
 * 
 * @author Andrew Cowie
 */
public class TextChain
{
    private Span[] spans;

    private int[] offsets;

    /**
     * cached length of this TextChain, in characters. A value of -1 means it
     * needs to be recalculated.
     */
    private int length = -1;

    public TextChain() {
        spans = new Span[0];

        offsets = new int[0];
    }

    TextChain(String str) {
        spans = new Span[1];
        spans[0] = Span.createSpan(str, null);

        offsets = new int[1];
        offsets[0] = 0;
    }

    TextChain(Span initial) {
        spans = new Span[1];
        spans[0] = initial;

        offsets = new int[1];
        offsets[0] = 0;
    }

    private void invalidateCache() {
        length = -1;
    }

    /**
     * Update the offset cache. Assumes the spans array has been set.
     */
    private void calculateOffsets() {
        int result;
        int i;

        offsets = new int[spans.length];

        result = 0;
        for (i = 0; i < spans.length; i++) {
            offsets[i] = result;
            result += spans[i].getWidth();
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
        Span s;
        int i, j, J;

        str = new StringBuilder();

        for (i = 0; i < spans.length; i++) {
            s = spans[i];
            J = s.getWidth();

            for (j = 0; j < J; j++) {
                str.appendCodePoint(s.getChar(j));
            }
        }

        return str.toString();
    }

    public void append(Span addition) {
        final Span[] replacement;
        final int[] cache;
        final int len, width;

        if (addition == null) {
            throw new IllegalArgumentException();
        }

        len = spans.length;

        /*
         * Handle empty TextChain case
         */

        if (len == 0) {
            spans = new Span[1];
            spans[0] = addition;

            offsets = new int[1];
            offsets[0] = 0;
            length = addition.getWidth();

            return;
        }

        /*
         * Otherwise, we are indeed appending.
         */

        replacement = new Span[len + 1];

        arraycopy(spans, 0, replacement, 0, len);
        replacement[len] = addition;
        spans = replacement;

        cache = new int[len + 1];
        arraycopy(offsets, 0, cache, 0, len);
        width = addition.getWidth();
        cache[len] = cache[len - 1] + width;
        offsets = cache;
        length += width;
    }

    /**
     * Insert the given Java String at the specified offset.
     */
    void insert(int offset, String what) {
        insert(offset, new Span[] {
            Span.createSpan(what, null)
        });
    }

    /**
     * Insert the given range of Spans at the specified offset.
     */
    void insert(final int offset, final Span[] addition) {
        int i, j, num;
        final Span original, before, after;
        final int point;
        final Span[] replacement;

        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        i = indexOfSpan(offset);

        if (i == spans.length) {
            // appending
            point = 0;
        } else {
            // inserting
            point = offset - offsets[i];
        }

        if (point == 0) {
            replacement = new Span[spans.length + addition.length];
        } else {
            replacement = new Span[spans.length + addition.length + 1];
        }

        /*
         * Copy portion of old array up to but not including index Span
         */

        num = i;
        if (num > 0) {
            arraycopy(spans, 0, replacement, 0, num);
        }

        /*
         * Copy portion of old array after index Span
         */

        num = spans.length - (i + 1);
        if (num > 0) {
            j = replacement.length - num;
            arraycopy(spans, i + 1, replacement, j, num);
        }

        /*
         * Either insert (added Spans) if at a boundary [ie point == 0] or
         * clove index Span in two, and insert (before Span, added Spans,
         * after Span).
         */

        if (point == 0) {
            num = addition.length;
            arraycopy(addition, 0, replacement, i, num);
            i += num;
        } else {
            original = spans[i];
            before = original.split(0, point);
            after = original.split(point);

            replacement[i] = before;
            i++;

            num = addition.length;
            arraycopy(addition, 0, replacement, i, num);
            i += num;

            replacement[i] = after;
        }

        spans = replacement;

        /*
         * FUTURE Since we know the point up to which we didn't change the
         * offsets, we could only invalidate from the index span, and use
         * arraycopy to clone the offsets array up to that point.
         */

        invalidateCache();
    }

    /**
     * Cut a Piece in two at point. Amend the linkages so that overall Text is
     * the same after the operation.
     */
    Piece splitAt(int index, int point) {
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

        before = span.split(0, point);
        after = span.split(point);

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
     * Find the Span beginning at or enclosing offset.
     */
    Span spanAt(final int offset) {
        final int index;

        if (spans.length == 0) {
            return null;
        } else {
            index = indexOfSpan(offset);
            if (index < spans.length) {
                return spans[index];
            } else {
                return null;
            }
        }
    }

    /**
     * Get the array index of the Span commencing or enclosing offset. Returns
     * <code>-1</code> if the offset represents the end of the TextChain.
     */
    /*
     * TODO implementation is an ugly linear search.
     */
    private int indexOfSpan(final int offset) {
        int start;
        int i, I;

        if (length == -1) {
            calculateOffsets();
        }

        if (offset == 0) {
            return 0;
        }

        I = spans.length;

        if (I == 1) {
            return 0;
        }

        if (offset == length) {
            return I;
        }

        if (offset > length) {
            throw new IndexOutOfBoundsException();
        }

        start = 0;

        for (i = 1; i < I; i++) {
            start = offsets[i];

            if (start > offset) {
                return i - 1;
            }
            if (start == offset) {
                return i;
            }
        }

        return I - 1;
    }

    /**
     * Find the Span containing offset, and split it into two. Handle the
     * [common] boundary cases of an offset at a Span boundary. Returns the
     * index of the Span where the new insertion point is.
     */
    int splitAt(int offset) {
        int start, following;

        if (offset == 0) {
            return 0;
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
     * Splice a Chunk into the Text.
     */
    void insert(int offset, Span addition) {
        insert(offset, new Span[] {
            addition
        });
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
        calculateOffsets();

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
        if (pair.one.offset < pair.two.offset) {
            return formArray(pair.one, pair.two);
        } else {
            return formArray(pair.two, pair.one);
        }
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

    public int wordBoundaryBefore(final int offset) {
        final Piece origin;

        if (length == -1) {
            calculateOffsets();
        }

        origin = pieceAt(offset);
        if (origin == null) {
            return 0;
        }

        return wordBoundaryBefore(origin, offset);
    }

    private int wordBoundaryBefore(final Piece origin, final int offset) {
        int start;
        Piece p;
        int i;
        boolean found;
        int ch; // switch to char if you're debugging.

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
                ch = p.span.getChar(i);
                if (!(Character.isLetter(ch) || (ch == '\''))) {
                    found = true;
                    break;
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

        return start;
    }

    /*
     * It would be nice to remove this
     */
    public int wordBoundaryAfter(final int offset) {
        final Piece origin;

        if (length == -1) {
            calculateOffsets();
        }

        origin = pieceAt(offset);
        if (origin == null) {
            return length;
        }

        return wordBoundaryAfter(origin, offset);
    }

    private int wordBoundaryAfter(final Piece origin, final int offset) {
        int end;
        Piece p;
        int i, len;
        boolean found;
        int ch;

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
                ch = p.span.getChar(i);
                if (!(Character.isLetter(ch) || (ch == '\''))) {
                    found = true;
                    break;
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

        return end;
    }

    /*
     * unused, but good code.
     */
    static String makeWordFromSpans(Extract extract) {
        final StringBuilder str;
        int i, I, j, J;
        Span s;

        I = extract.size();

        if (I == 1) {
            return extract.get(0).getText();
        } else {
            str = new StringBuilder();

            for (i = 0; i < I; i++) {
                s = extract.get(i);

                J = s.getWidth();

                for (j = 0; j < J; j++) {
                    str.appendCodePoint(s.getChar(j));
                }
            }

            return str.toString();
        }
    }

    /*
     * this could well become the basis of a public API
     */
    static String makeWordFromSpans(Pair pair) {
        final Piece alpha, omega;
        final StringBuilder str;
        int j, J;
        Piece p;
        Span s;

        alpha = pair.one;
        omega = pair.two;

        if (alpha == omega) {
            return alpha.span.getText();
        } else {
            str = new StringBuilder();

            p = alpha;
            while (p != null) {
                s = p.span;
                J = s.getWidth();

                for (j = 0; j < J; j++) {
                    str.appendCodePoint(s.getChar(j));
                }

                if (p == omega) {
                    break;
                }
                p = p.next;
            }

            return str.toString();
        }
    }

    /**
     * Given a cursor location in offset, work backwards to find a word
     * boundary, and then forwards to the next word boundary, and return the
     * word contained between those two points.
     */
    /*
     * After a huge amount of work, the current implementation in
     * EditorTextView doesn't call this, but exercises a similar algorithm.
     * Nevertheless this is heavily tested code, and we will probably return
     * to this "pick word from offset" soon.
     */
    String getWordAt(final int offset) {
        final Piece origin;
        int start, end;
        int i;
        Pair pair;
        int ch;

        if (length == -1) {
            calculateOffsets();
        }

        if (offset == length) {
            return null;
        }

        origin = pieceAt(offset);

        i = offset - origin.offset;
        ch = origin.span.getChar(i);
        if (!Character.isLetter(ch)) {
            return null;
        }

        start = wordBoundaryBefore(origin, offset);
        end = wordBoundaryAfter(origin, offset);

        /*
         * Now pull out the word
         */

        pair = extractFrom(start, end - start);
        return makeWordFromSpans(pair);
    }
}
