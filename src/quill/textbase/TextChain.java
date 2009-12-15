/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.textbase;

/**
 * A mutable buffer of unicode text which manages a binary tree of Spans in
 * order to maximize sharing of character array storage while giving us
 * efficient lookup by offset.
 * 
 * @author Andrew Cowie
 */
public class TextChain
{
    Node root;

    public TextChain() {
        root = null;
    }

    TextChain(final String str) {
        final Span span;

        span = Span.createSpan(str, null);
        root = new Node(span);
    }

    TextChain(Span initial) {
        root = new Node(initial);
    }

    /**
     * The length of this Text, in characters.
     */
    public int length() {
        if (root == null) {
            return 0;
        } else {
            return root.getWidth();
        }
    }

    /**
     * This is ineffecient! Use for debugging purposes only.
     */
    public String toString() {
        final StringBuilder str;

        if (root == null) {
            return "";
        }

        str = new StringBuilder();

        root.visitAll(new Visitor() {
            public void visit(Span span) {
                str.append(span.getText()); // TODO loop chars?
            }
        });

        return str.toString();
    }

    /*
     * This is an inefficient implementation!
     */
    public void append(Span addition) {
        if (addition == null) {
            throw new IllegalArgumentException();
        }

        /*
         * Handle empty TextChain case
         */

        if (root == null) {
            root = new Node(addition);
            return;
        }

        /*
         * Otherwise, we are appending. Hop to the end.
         */

        root = root.append(addition);
    }

    /*
     * TODO if we change to an EmptyNode singleton, then it should be returned
     * if empty.
     */
    Node getTree() {
        return root;
    }

    /**
     * Get the Span at a given offset, for testing purposes.
     */
    Span spanAt(int offset) {
        if (root == null) {
            if (offset == 0) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }
        return root.getSpanAt(offset);
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
     * splitAt().
     * 
     * Returns null if the offset is the end of the Chain. The end of the
     * chain is null if the length of the Chain is zero, the null Piece is
     * still the "end".
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
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (root == null) {
            if (offset != 0) {
                throw new IndexOutOfBoundsException();
            }
            root = new Node(addition);
            return;
        }

        root = root.insertSpanAt(offset, addition);
    }

    /**
     * Get a tree representing the concatonation of the marked up Spans in a
     * given range.
     */
    Node extractFrom(int offset, int width) {
        if (width == 0) {
            return null;
        }

        return root.subset(offset, width);
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

        preceeding = pair.one.prev;
        following = pair.two.next;

        if (offset == 0) {
        } else {
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
        Span span;

        span = root.getSpanAt(offset);

        if (span == null) {
            return null;
        } else {
            return span.getMarkup();
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
        final Node node;

        if (width < 0) {
            throw new IllegalArgumentException();
        }
        if (width == 0) {
            return new Extract();
        }

        node = extractFrom(start, width);
        return new Extract(node);
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

        if (root == null) {
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
            return length;
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
