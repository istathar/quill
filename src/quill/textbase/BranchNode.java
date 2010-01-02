/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

    /**
     * Get a representation of this Node showing Node left, and Node right
     * each delimited by «». Use for debugging purposes only!
     */
    /*
     * This is ineffecient!
     */
    public String toString() {
        final StringBuilder str;
        final CharacterVisitor tourist;

        str = new StringBuilder();

        tourist = new CharacterVisitor() {
            public boolean visit(int character, Markup markup) {
                str.appendCodePoint(character);
                return false;
            }
        };

        str.append("«");
        left.visitAll(tourist);
        str.append("»\n");

        str.append("«");
        right.visitAll(tourist);
        str.append("»");

        return str.toString();
    }

    Span getSpanAt(final int offset) {
        final int widthLeft;

        if (offset == width) {
            return null;
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        widthLeft = left.getWidth();

        if (offset < widthLeft) {
            return left.getSpanAt(offset);
        } else {
            return right.getSpanAt(offset - widthLeft);
        }
    }

    Node insertTreeAt(int offset, Node tree) {
        final int widthLeft;
        int point;
        final Node gauche, droit;

        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        /*
         * Insert at beginning, and append to end. What about balancing?
         */

        if (offset == 0) {
            return new BranchNode(tree, this);
        }
        if (offset == width) {
            return new BranchNode(this, tree);
        }

        widthLeft = left.getWidth();

        if (offset < widthLeft) {
            gauche = left.insertTreeAt(offset, tree);
            return new BranchNode(gauche, right);
        }

        point = offset - widthLeft;

        if (point > 0) {
            droit = right.insertTreeAt(point, tree);
            return new BranchNode(left, droit);
        } else if (point == 0) {
            if (left.getHeight() > right.getHeight()) {
                droit = new BranchNode(tree, right);
                return new BranchNode(left, droit);
            } else {
                gauche = new BranchNode(left, tree);
                return new BranchNode(gauche, right);
            }
        }

        throw new IllegalStateException();
    }

    Node subset(int offset, int wide) {
        final int widthLeft;
        int across;
        Node gauche, droit;

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

        if ((offset == 0) && (wide == width)) {
            return this;
        }

        if (wide == 0) {
            return EMPTY;
        }

        /*
         * Ok, we have a valid range. Is that range entirely left or entirely
         * right?
         */

        widthLeft = left.getWidth();

        if (offset + wide <= widthLeft) {
            return left.subset(offset, wide);
        }

        if (offset > widthLeft) {
            return right.subset(offset - widthLeft, wide);
        }

        /*
         * So now we know it's at least partially overlapping left and right.
         * Do the left sub part first
         */

        // how many characters?
        across = widthLeft - offset;
        gauche = left.subset(offset, across);

        /*
         * Now the right sub part.
         */

        across = wide - across;
        droit = right.subset(0, across);

        return new BranchNode(gauche, droit);
    }

    boolean visitRange(final CharacterVisitor tourist, final int offset, final int wide) {
        int start, across, consumed;
        final int widthLeft, widthRight;

        consumed = 0;

        widthLeft = left.getWidth();

        if ((offset == 0) && (wide >= widthLeft)) {
            if (left.visitAll(tourist)) {
                return true;
            }
            consumed = widthLeft;
        } else if (offset < widthLeft) {
            start = offset;
            if (offset + wide > widthLeft) {
                across = widthLeft - offset;
            } else {
                across = wide;
            }

            if (left.visitRange(tourist, start, across)) {
                return true;
            }
            consumed = across;
        }

        if (consumed == wide) {
            return false;
        }

        widthRight = right.getWidth();

        if (wide - consumed == widthRight) {
            if (right.visitAll(tourist)) {
                return true;
            }
        } else {
            start = offset + consumed - widthLeft;
            across = wide - consumed;
            if (right.visitRange(tourist, start, across)) {
                return true;
            }
        }
        return false;
    }

    int getWordBoundaryBefore(final int offset) {
        final int widthLeft;
        final int result;

        /*
         * Unlike the after case below, we _do_ have to check position zero.
         */

        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        widthLeft = left.getWidth();

        /*
         * If it's on the left, then pass the search down, and we're done
         * here.
         */

        if (offset < widthLeft) {
            return left.getWordBoundaryBefore(offset);
        }

        /*
         * If the offset is on the right, pass it down, then continue down
         * left if we come back.
         */

        result = right.getWordBoundaryBefore(offset - widthLeft);
        if (result > -1) {
            return widthLeft + result;
        }

        return left.getWordBoundaryBefore(widthLeft);
    }

    int getWordBoundaryAfter(final int offset) {
        final int widthLeft;
        int result;

        /*
         * If the requested offset is already the end, then we can return
         * "not found"
         */
        if (offset == width) {
            return -1;
        }

        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        widthLeft = left.getWidth();

        /*
         * If it's on the left, then pass the search down, otherwise run
         * through the right.
         */

        if (offset < widthLeft) {
            result = left.getWordBoundaryAfter(offset);
            if (result > -1) {
                return result;
            }
            result = right.getWordBoundaryAfter(0);
            if (result > -1) {
                return widthLeft + result;
            }
        } else {
            result = right.getWordBoundaryAfter(offset - widthLeft);
            if (result > -1) {
                return widthLeft + result;
            }
        }

        // not found
        return -1;
    }

    Node rebalance() {
        int heightLeft, heightRight;

        heightLeft = left.getHeight();
        heightRight = right.getHeight();

        if (heightLeft >= heightRight + 2) {
            return left.rotateLeft(right);
        } else if (heightRight >= heightLeft + 2) {
            return right.rotateRight(left);
        } else {
            return this;
        }
    }

    /**
     * <pre>
     *       C
     *      / \
     *     B  N4          B'
     *    / \           /   \
     *   A  N3   ->    A     C'
     *  / \           / \   / \
     * N1 N2         N1 N2 N3 N4
     * </pre>
     * 
     * C calls this on B (with N4), returns B'
     */
    Node rotateLeft(Node N4) {
        Node A, Bp, Cp;

        A = left;
        Cp = new BranchNode(right, N4);
        Bp = new BranchNode(A, Cp);

        return Bp;
    }

    /**
     * <pre>
     *       C
     *      / \
     *     B  N4          B'
     *    / \           /   \
     *   A  N3   ->    A     C'
     *  / \           / \   / \
     * N1 N2         N1 N2 N3 N4
     * </pre>
     * 
     * <pre>
     *   A
     *  / \
     * N1  B              B'
     *    / \           /   \
     *   N2  C   ->    A'    C
     *      / \       / \   / \
     *     N3 N4     N1 N2 N3 N4
     * </pre>
     * 
     * 
     * A calls this on B (with N1), returns B'
     */
    Node rotateRight(Node N1) {
        Node Ap, Bp, C;

        C = right;
        Ap = new BranchNode(N1, left);
        Bp = new BranchNode(Ap, C);

        return Bp;
    }
}
