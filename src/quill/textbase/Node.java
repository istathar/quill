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
    private final int depth;

    /**
     * The distance off the beginning of the backing Span tree this Node
     * begins, in characters.
     */
    /*
     * TODO Do we need this?
     */
    private final int offset;

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
        depth = 1;
        offset = 0;
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
        final int depthLeft, depthRight;
        final int widthLeft, widthRight;

        if (alpha == null) {
            depthLeft = 0;
            widthLeft = 0;
        } else {
            depthLeft = alpha.getDepth();
            widthLeft = alpha.getWidth();
        }

        if (omega == null) {
            depthRight = 0;
            widthRight = 0;
        } else {
            depthRight = omega.getDepth();
            widthRight = omega.getWidth();
        }

        if (depthLeft == depthRight) {
            depth = depthLeft + 1;
        } else if (depthLeft > depthRight) {
            depth = depthLeft;
        } else {
            depth = depthRight;
        }

        offset = 0;

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
    int getDepth() {
        return depth;
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

    Span getSpanAt(final int offset) {
        final int widthLeft, widthCenter, widthRight;

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

        widthCenter = this.getWidth();
        if (offset - widthLeft <= widthCenter) {
            return data;
        } else {
            return right.getSpanAt(offset - widthCenter - widthLeft);
        }
    }
}

interface Visitor
{
    void visit(Span span);
}
