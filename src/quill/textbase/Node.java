/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
 * Base class of the binary tree we use for managing lists of Spans.
 * 
 * @author Andrew Cowie
 */
class Node
{
    /**
     * Height of the tree.
     */
    private final int height;

    /**
     * The distance off the beginning of the backing Span tree this Node
     * begins, in characters.
     */
    /*
     * TODO Do we need this?
     */

    /**
     * Width of this tree, in characters.
     */
    private final int width;

    /**
     * The binary tree below and preceeding this Node.
     */
    private final Node left;

    /**
     * This Node's content.
     */
    private final Span data;

    /**
     * The binary tree below and following this Node.
     */
    private final Node right;

    private Node(Span span) {
        height = 1;
        width = span.getWidth();
        data = span;
        left = null;
        right = null;
    }

    private Node(Node alpha, Span span, Node omega) {
        final int heightLeft, heightRight;
        final int widthLeft, widthRight, widthCenter;

        if (alpha == null) {
            heightLeft = 0;
            widthLeft = 0;
        } else {
            heightLeft = alpha.getHeight();
            widthLeft = alpha.getWidth();
        }

        if (omega == null) {
            heightRight = 0;
            widthRight = 0;
        } else {
            heightRight = omega.getHeight();
            widthRight = omega.getWidth();
        }

        height = Math.max(heightLeft, heightRight) + 1;

        left = alpha;
        right = omega;

        if (span == null) {
            data = null;
            widthCenter = 0;
        } else {
            data = span;
            widthCenter = span.getWidth();
        }

        width = widthLeft + widthCenter + widthRight;
    }

    /**
     * Given a single Span, create a tree. Used in testing.
     */
    static Node create(Span span) {
        if (span == null) {
            throw new IllegalArgumentException();
        }
        return new Node(span);
    }

    /**
     * Create a new tree with the given binary trees below before and after
     * this Node, but with no content of its own. Used for adjoining two
     * trees.
     */
    static Node create(Node right, Node left) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        }
        return new Node(right, null, left);
    }

    /**
     * Create a new Node with the given Span as content and the given binary
     * trees below before and after this Node. Used when inserting.
     */
    static Node create(Node left, Span span, Node right) {
        if ((left == null) && (right == null)) {
            return new Node(span);
        }
        if (span == null) {
            if (right == null) {
                return left;
            }
            if (left == null) {
                return right;
            }
        }
        return new Node(left, span, right);
    }

    /**
     * Get the width of this node (and its descendents), in characters.
     */
    public int getWidth() {
        return width;
    }

    /**
     * How many levels to the base of the tree? Leaves are 1, and emtpy is 0.
     */
    int getHeight() {
        return height;
    }

    Span getSpan() {
        return data;
    }

    Node append(final Span addition) {
        if (addition == null) {
            throw new IllegalArgumentException();
        }
        if (data == null) {
            return new Node(addition);
        } else {
            return new Node(this, addition, null);
        }
    }

    /**
     * Invoke tourist's visit() method for each Span in the tree, in-order
     * traversal.
     */
    public void visitAll(final SpanVisitor tourist) {
        if (left != null) {
            left.visitAll(tourist);
        }
        if (data != null) {
            tourist.visit(data);
        }
        if (right != null) {
            right.visitAll(tourist);
        }
    }

    /**
     * Invoke tourist's visit() method for each character in the tree,
     * in-order traversal.
     */
    public void visitAll(final CharacterVisitor tourist) {
        final int I;
        int i, ch;
        Markup m;

        if (left != null) {
            left.visitAll(tourist);
        }
        if (data != null) {
            I = data.getWidth();
            m = data.getMarkup();
            for (i = 0; i < I; i++) {
                ch = data.getChar(i);
                tourist.visit(ch, m);
            }
        }
        if (right != null) {
            right.visitAll(tourist);
        }
    }

    /**
     * Call tourist's visit method for each character in the tree <b>from
     * start for width</b>, in-order traversal,
     */
    public void visitRange(final CharacterVisitor tourist, final int offset, final int wide) {
        int i, start, across, consumed;
        final int I, widthLeft, widthCenter, widthRight;
        int ch;
        Markup m;

        consumed = 0;

        if (left != null) {
            widthLeft = left.getWidth();
            if ((offset == 0) && (wide >= widthLeft)) {
                left.visitAll(tourist);
                consumed = widthLeft;
            } else if (offset < widthLeft) {
                start = offset;
                across = widthLeft - offset;
                left.visitRange(tourist, start, across);
                consumed = across;
            }
        } else {
            widthLeft = 0;
        }
        if ((data != null) && (consumed != wide)) {
            widthCenter = data.getWidth();
            m = data.getMarkup();

            if (offset < widthLeft + widthCenter) {
                if (offset > widthLeft) {
                    start = offset - widthLeft;
                } else {
                    start = 0;
                }
                across = wide - consumed;

                if (across > widthCenter) {
                    across = widthCenter;
                    consumed += widthCenter;
                } else {
                    consumed += across;
                }
                for (i = start; i < start + across; i++) {
                    ch = data.getChar(i);
                    tourist.visit(ch, m);
                }
            }
        } else {
            widthCenter = 0;
        }
        if ((right != null) && (consumed != wide)) {
            widthRight = right.getWidth();
            if ((offset == widthLeft + widthCenter) && (wide == widthRight)) {
                right.visitAll(tourist);
            } else {
                start = offset - (widthLeft + widthCenter);
                across = wide - consumed;
                right.visitRange(tourist, start, across);
            }
        }
    }

    /**
     * This is ineffecient! Use for debugging purposes only.
     */
    public String toString() {
        final StringBuilder str;
        final CharacterVisitor tourist;

        str = new StringBuilder();

        tourist = new CharacterVisitor() {
            public void visit(int character, Markup markup) {
                str.appendCodePoint(character);
            }
        };

        str.append("«");
        if (left != null) {
            left.visitAll(tourist);
        }
        str.append("»\n");

        str.append("«");
        if (data != null) {
            str.append(data.getText());
        }
        str.append("»\n");

        str.append("«");
        if (right != null) {
            right.visitAll(tourist);
        }
        str.append("»\n");

        return str.toString();
    }

    Span getSpanAt(final int offset) {
        final int widthLeft, widthCenter;

        if (offset == width) {
            return null;
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
        }

        if (offset < widthLeft) {
            return left.getSpanAt(offset);
        }

        widthCenter = data.getWidth();
        if (offset - widthLeft <= widthCenter) {
            return data;
        } else {
            return right.getSpanAt(offset - widthCenter - widthLeft);
        }
    }

    Node insertSpanAt(final int offset, final Span addition) {
        final int widthLeft, widthCenter;
        final Span before, after;
        int point;
        final Node gauche, droit;

        if (offset == width) {
            return new Node(this, addition, null);
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
        }

        if (offset < widthLeft) {
            gauche = left.insertSpanAt(offset, addition);
            return new Node(gauche, data, right);
        }

        point = offset - widthLeft;
        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }
        if (point > widthCenter) {
            droit = right.insertSpanAt(point - widthCenter, addition);
            return new Node(left, data, droit);
        }

        if (point == 0) {
            droit = new Node(null, data, right);

            return new Node(left, addition, droit);
        } else if (point == widthCenter) {
            gauche = new Node(left, data, null);

            return new Node(gauche, addition, right);
        } else {
            before = data.split(0, point);
            after = data.split(point);

            gauche = new Node(left, before, null);
            droit = new Node(null, after, right);

            return new Node(gauche, addition, droit);
        }
    }

    /**
     * Get a subset of this tree from offset, wide characters in width.
     */
    Node subset(int offset, int wide) {
        final int widthLeft, widthCenter;
        int amount, begin, end;
        Node gauche, droit;
        Span span;

        /*
         * Boundary checks
         */

        if (offset < 0) {
            throw new IndexOutOfBoundsException("negative offset illegal");
        }
        if (width < 0) {
            throw new IndexOutOfBoundsException("can't subset a negative number of characters");
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException("offset too high");
        }
        if (offset + wide > width) {
            throw new IndexOutOfBoundsException(
                    "requested number of characters greater than available text");
        }

        /*
         * Handle corner cases. If we ever adapt this into something that is
         * subclassed rather than all-in-one, then some of this logic will
         * move.
         */

        if ((offset == 0) && (wide == width)) {
            return this;
        }

        if (wide == 0) {
            return null;
        }

        /*
         * Ok, we have a valid range. Is that range entirely left or entirely
         * right?
         */

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
            if (offset + wide < widthLeft) {
                return left.subset(offset, wide);
            }
        }

        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }

        if (right != null) {
            if (offset > widthLeft + widthCenter) {
                return right.subset(width - offset, wide);
            }
        }

        /*
         * Entirely center?
         */

        if (wide <= widthCenter) {
            begin = offset - widthLeft;
            end = begin + wide;
            span = data.split(begin, end);
            return new Node(span);
        }

        /*
         * So now we know it's at least partially overlapping either left or
         * right.
         */

        if (offset < widthLeft) {
            // how many characters?
            amount = widthLeft - offset;
            gauche = left.subset(offset, amount);
            // how many characters remain?
            amount = wide - amount;
        } else {
            gauche = null;
            amount = wide;
        }

        if (amount == widthCenter) {
            span = data;
            droit = null;
        } else if (amount < widthCenter) {
            span = data.split(0, amount);
            droit = null;
        } else {
            span = data;
            droit = right.subset(0, amount - widthCenter);
        }

        return new Node(gauche, span, droit);
    }
}

/**
 * Visit the Spans in a range, in order.
 * 
 * @author Andrew Cowie
 */
interface SpanVisitor
{
    void visit(Span span);
}

/**
 * Visit the characters in a range, one by one, in order.
 * 
 * @author Andrew Cowie
 */
interface CharacterVisitor
{
    /**
     * @param character
     *            the Unicode codepoint at this offset
     * @param markup
     *            the Markup formatting applicable at this offset.
     */
    void visit(int character, Markup markup);
}
