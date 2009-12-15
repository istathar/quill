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
        root = Node.create(span);
    }

    TextChain(Span initial) {
        root = Node.create(initial);
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

        root.visitAll(new SpanVisitor() {
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
            root = Node.create(addition);
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
    /*
     * FIXME replace this with (int, Node) since we're always just pulling the
     * Span[] out of Extract to call this. TODO when we change Extract to
     * <Node> rather than Span[]
     */
    protected void insert(int offset, Span[] range) {
        if (offset < 0) {
            throw new IllegalArgumentException();
        }

        /*
         * Create the insertion point
         */

        /*
         * Create and insert Pieces wrapping the Spans.
         */

        /*
         * And correct the linkages in the reverse direction
         */
        throw new Error("FIXME");
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
            root = Node.create(addition);
            return;
        }

        root = root.insertSpanAt(offset, addition);
    }

    /**
     * Get a tree representing the concatonation of the marked up Spans in a
     * given range.
     * 
     * @deprecated use Node's subset() directly.
     */
    Node extractFrom(int offset, int wide) {
        return root.subset(offset, wide);
    }

    public Extract extractAll() {
        return new Extract(root);
    }

    /**
     * Delete a width wide segment starting at offset. Because people have to
     * call extractRange() right before this in order to create a
     * DeleteChange, this will duplicate effort. So, TODO create one which
     * passes in a tree of the known bit to be removed.
     */
    protected void delete(final int offset, final int wide) {
        final Node preceeding, following;
        final int start, across;

        if (root == null) {
            throw new IllegalStateException("Can't delete when already emtpy");
        }
        if (wide == 0) {
            throw new IllegalArgumentException("Can't delete nothing");
        }

        /*
         * Handle the special case of deleting everything.
         */

        if ((offset == 0) && (wide == root.getWidth())) {
            root = null;
            return;
        }

        /*
         * Create subtrees for everything before and after the deletion range
         */

        if (offset > 0) {
            preceeding = root.subset(0, offset);
        } else {
            preceeding = null;
        }

        start = offset + wide;
        across = root.getWidth() - start;
        following = root.subset(start, across);

        /*
         * Now combine these subtrees to effect the deletion.
         */

        root = Node.create(preceeding, following);
    }

    /**
     * Add or remove a Markup format from a range of text.
     */
    protected void format(int offset, int width, Markup format) {
        Node tree;
        Span s;

        tree = extractFrom(offset, width);

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
        Node tree;
        Span s;

        tree = extractFrom(offset, width);

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
        Node tree;
        Span s;

        tree = extractFrom(offset, width);

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
    public Extract extractRange(int start, int wide) {
        final Node node;

        if (wide < 0) {
            throw new IllegalArgumentException();
        }
        if (wide == 0) {
            return new Extract();
        }

        node = root.subset(start, wide);
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
    static String makeWordFromSpans(Node tree) {
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
        int start, end;
        int i;
        int ch;

        if (offset == root.getWidth()) {
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
