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

    /**
     * Given a single Span, create a tree. Used in testing.
     */
    Node(Span span) {
        height = 1;
        width = span.getWidth();
        data = span;
        left = null;
        right = null;
    }

    /**
     * Create a new Node with the given Span as content and the given binary
     * trees below before and after this Node.
     */
    Node(Node alpha, Span span, Node omega) {
        final int heightLeft, heightRight;
        final int widthLeft, widthRight;

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
        data = span;
        right = omega;

        width = widthLeft + span.getWidth() + widthRight;
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

    Node append(Span addition) {
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
    public void visitAll(Visitor tourist) {
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
     * Call tourist's visit method for each Span in the tree <b>from start for
     * width</b>, in-order traversal,
     */
    // FIXME HERE
    public void visitRange(final Visitor tourist, final int start, final int wide) {
        int point;

        if (left != null) {
            if (start < left.getWidth()) {
                left.visitAll(tourist);
            }
            point = start - left.getWidth();
        }
        if (data != null) {
            tourist.visit(data);
        }
        if (right != null) {
            right.visitAll(tourist);
        }
    }

    /**
     * This is ineffecient! Use for debugging purposes only.
     */
    public String toString() {
        final StringBuilder str;

        str = new StringBuilder();

        this.visitAll(new Visitor() {
            public void visit(Span span) {
                str.append(span.getText()); // TODO loop chars?
            }
        });

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
        widthCenter = data.getWidth();
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

        if (offset < 0) {
            throw new IndexOutOfBoundsException("negative offset illegal");
        }
        if (width < 0) {
            throw new IndexOutOfBoundsException("can't subset a negative number of characters");
        }
        if (offset >= width) {
            throw new IndexOutOfBoundsException("offset too high");
        }
        if (offset + wide > width) {
            throw new IndexOutOfBoundsException(
                    "requested number of characters greater than available text");
        }

        /*
         * Entirely left or entirely right?
         */

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
            if (offset + wide < widthLeft) {
                return left.subset(offset, wide);
            }
        }

        widthCenter = data.getWidth();

        if (right != null) {
            if (offset > widthLeft + widthCenter) {
                return right.subset(offset, wide);
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
            amount = wide - (left.width - offset);
            gauche = left.subset(offset, amount);
            // how many characters remain?
            amount = wide - amount;
        } else {
            gauche = null;
            amount = wide;
        }

        if (amount < data.getWidth()) {
            span = data.split(0, amount);
            droit = null;
        } else {
            span = data;
            droit = right.subset(0, amount - data.getWidth());
        }

        return new Node(gauche, span, droit);
    }
}

interface Visitor
{
    void visit(Span span);
}
