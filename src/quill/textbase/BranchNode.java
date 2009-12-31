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

final class BranchNode extends Node
{
    /**
     * Cached height of the tree.
     */
    private final int height;

    /**
     * Cached width of the content.
     */
    private final int width;

    /**
     * The binary tree below and preceeding this Node.
     */
    private final Node left;

    /**
     * The binary tree below and following this Node.
     */
    private final Node right;

    BranchNode(Node alpha, Node omega) {
        super();
        final int heightLeft, heightRight;
        final int widthLeft, widthRight;

        left = alpha;
        right = omega;

        heightLeft = alpha.getHeight();
        widthLeft = alpha.getWidth();

        heightRight = omega.getHeight();
        widthRight = omega.getWidth();

        height = Math.max(heightLeft, heightRight) + 1;

        width = widthLeft + widthRight;
    }

    public int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    Node getLeft() {
        return left;
    }

    Node getRight() {
        return right;
    }

    Node append(Span addition) {
        /*
         * Strange situation, but if we have empty nodes we can shortcut.
         */

        if ((left == EMPTY) && (right == EMPTY)) {
            return new LeafNode(addition);
        }

        /*
         * This node left half full, but right is available. So just plug it
         * in.
         */

        if ((left != EMPTY) && (right == EMPTY)) {
            return new BranchNode(left, new LeafNode(addition));
        }

        /*
         * This node right half full, so descend recursively and append.
         */

        if ((left == EMPTY) && (right != EMPTY)) {
            return right.append(addition);
        }

        /*
         * This node full, so grow in height one.
         */

        if (left.getHeight() > right.getHeight()) {
            return new BranchNode(left, right.append(addition));
        } else {
            return new BranchNode(this, new LeafNode(addition));
        }
    }

    boolean visitAll(SpanVisitor tourist) {
        if (left.visitAll(tourist)) {
            return true;
        }

        if (right.visitAll(tourist)) {
            return true;
        }

        return false;
    }

    boolean visitAll(final CharacterVisitor tourist) {
        if (left.visitAll(tourist)) {
            return true;
        }

        if (right.visitAll(tourist)) {
            return true;
        }

        return false;
    }

}
