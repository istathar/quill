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

import java.util.ArrayList;

/**
 * A list of Spans.
 * 
 * <p>
 * To access the Spans in this tree, use the visitor pattern via visitAll() or
 * visitRange().
 * 
 * <p>
 * This is implemented as a b-tree, with Node left, Span center, Node right.
 * Most of the useful operations on trees are expressed on the
 * {@link TextChain} class, which wraps one of these to back a Segment and its
 * Editor.
 * 
 * 
 * @author Andrew Cowie
 */
/*
 * If this becomes an abstract base class, then create a new public class
 * called Tree and replace public usage of Node with it.
 */
abstract class Node extends Extract
{
    /*
     * For cases where an empty Extract has to be returned, specifically
     * TextChain's extractParagraphs().
     */
    protected static final Node EMPTY;

    static {
        EMPTY = new EmptyNode();
    }

    protected Node() {}

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
     * An empty tree.
     */
    static Node createNode() {
        return EMPTY;
    }

    /**
     * Given a single Span, create a tree. Used in testing, and for the
     * clipboard cases.
     */
    /*
     * Called from Extract.create().
     */
    static Node createNode(Span span) {
        if (span == null) {
            throw new IllegalArgumentException();
        }
        return new LeafNode(span);
    }

    /**
     * Create a new tree with the given binary trees below before and after
     * this Node, but with no content of its own. Used for adjoining two
     * trees.
     */
    static Node createNode(Node right, Node left) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        }
        return new BranchNode(right, left);
    }

    /**
     * Create a new Node with the given Span as content and the given binary
     * trees below before and after this Node. Used when inserting.
     */
    static Node createNode(Node left, Span span, Node right) {
        if ((left == null) && (right == null)) {
            return new LeafNode(span);
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
    public abstract int getWidth();

    /**
     * How many levels to the base of the tree? Leaves are 1, and emtpy is 0.
     */
    abstract int getHeight();

    /**
     * Get a String of the characters in this Node and its descendants. Only
     * use this for minor cases (ie copy to clipboard). Regular usage should
     * visit() across the characters or Spans.
     */
    /*
     * Same inefficient implementation as toString(), without the extra
     * debugging delimiters.
     */
    public String getText() {
        final StringBuilder str;
        final CharacterVisitor tourist;

        str = new StringBuilder();

        tourist = new CharacterVisitor() {
            public boolean visit(int character, Markup markup) {
                str.appendCodePoint(character);
                return false;
            }
        };

        this.visit(tourist);

        return str.toString();

    }

    Node append(final Span addition) {
        if (addition == null) {
            throw new IllegalArgumentException();
        }

        /*
         * Completely empty node, or single Span node. Grow height by one.
         */

        if ((left == null) && (right == null)) {
            if (data == null) {
                return new Node(addition);
            } else {
                return new Node(this, addition, null);
            }
        }

        /*
         * This node full, so grow in height one.
         */

        if ((left != null) && (right != null)) {
            if (left.getHeight() > right.getHeight()) {
                return new Node(left, data, right.append(addition));
            } else {
                return new Node(this, addition, null);
            }
        }

        /*
         * This node left half full
         */

        if ((left != null) && (right == null)) {
            if (data == null) {
                return new Node(left, addition, null);
            } else {
                return new Node(left, data, new Node(addition));
            }
        }

        /*
         * This node right half full, so descend recursively and append.
         */

        if ((left == null) && (right != null)) {
            if (data == null) {
                // rare?
                return right.append(addition);
            } else {
                return new Node(null, data, right.append(addition));
            }

        }

        throw new IllegalStateException();
    }

    /**
     * Invoke tourist's visit() method for each Span in the tree, in-order
     * traversal.
     */
    abstract boolean visitAll(final SpanVisitor tourist);

    public void visit(final SpanVisitor tourist) {
        visitAll(tourist);
    }

    /**
     * Invoke tourist's visit() method for each character in the tree,
     * in-order traversal.
     */
    abstract boolean visitAll(final CharacterVisitor tourist);

    public void visit(final CharacterVisitor tourist) {
        visitAll(tourist);
    }

    /**
     * Call tourist's visit() method for each character in the tree from start
     * for wide characters, traversing the tree in-order.
     */
    private boolean visitRange(final CharacterVisitor tourist, final int offset, final int wide) {
        int i, start, across, consumed;
        final int widthLeft, widthCenter, widthRight;
        int ch;
        Markup m;

        consumed = 0;

        if (left != null) {
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

                if (start + across > widthCenter) {
                    across = widthCenter - start;
                }
                for (i = start; i < start + across; i++) {
                    ch = data.getChar(i);
                    if (tourist.visit(ch, m)) {
                        return true;
                    }
                }
                consumed += across;
            }
        } else {
            widthCenter = 0;
        }
        if ((right != null) && (consumed != wide)) {
            widthRight = right.getWidth();
            if ((offset + consumed == widthLeft + widthCenter) && (wide - consumed == widthRight)) {
                if (right.visitAll(tourist)) {
                    return true;
                }
            } else {
                start = (offset + consumed) - (widthLeft + widthCenter);
                across = wide - consumed;
                if (right.visitRange(tourist, start, across)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void visit(final CharacterVisitor tourist, final int offset, final int wide) {
        visitRange(tourist, offset, wide);
    }

    /**
     * Get a representation of this Node showing Node left, Span center and
     * Node right each delimited by «». Use for debugging purposes only!
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
        if (left != null) {
            left.visit(tourist);
        }
        str.append("»\n");

        str.append("«");
        if (data != null) {
            str.append(data.getText());
        }
        str.append("»\n");

        str.append("«");
        if (right != null) {
            right.visit(tourist);
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

        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }

        if (offset - widthLeft < widthCenter) {
            return data;
        } else {
            return right.getSpanAt(offset - widthCenter - widthLeft);
        }
    }

    /*
     * FUTURE It may be that nothing in the production code actually uses
     * this! Given how single Span creation is the common case for the user
     * typing, if we fix up InsertTextChange to persist just Spans (rather
     * than automatically putting them into an Extract) or alternately stop
     * using Changes then we can probably use this for real.
     */
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

    /*
     * Logic cloned from insertSpanAt() above.
     */
    Node insertTreeAt(int offset, Node tree) {
        final int widthLeft, widthCenter;
        int point;
        final Node gauche, droit;
        final Span before, after;

        if (offset == 0) {
            return new Node(tree, null, this);
        }
        if (offset == width) {
            return new Node(this, null, tree);
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
            gauche = left.insertTreeAt(offset, tree);
            return new Node(gauche, data, right);
        }

        point = offset - widthLeft;
        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }
        if (point > widthCenter) {
            droit = right.insertTreeAt(point - widthCenter, tree);
            return new Node(left, data, droit);
        }

        if (point == 0) {
            gauche = new Node(left, null, tree);
            droit = new Node(null, data, right);

            return new Node(gauche, null, droit);
        } else if (point == widthCenter) {
            gauche = new Node(left, data, null);
            droit = new Node(tree, null, right);

            return new Node(gauche, null, droit);
        } else {
            before = data.split(0, point);
            after = data.split(point);

            gauche = new Node(left, before, tree);
            droit = new Node(null, after, right);

            return new Node(gauche, null, droit);
        }
    }

    /**
     * Get a subset of this tree from offset, wide characters in width.
     */
    Node subset(int offset, int wide) {
        final int widthLeft, widthCenter;
        int consumed, across, begin, end;
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

        widthLeft = getLeft().getWidth();

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
            if (offset + wide <= widthLeft) {
                return left.subset(offset, wide);
            }
        }

        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }

        if (right != null) {
            if (offset >= widthLeft + widthCenter) {
                return right.subset(offset - widthLeft - widthCenter, wide);
            }
        }

        /*
         * Entirely center?
         */

        if ((offset >= widthLeft) && (offset < widthLeft + widthCenter) && (wide <= widthCenter)) {
            begin = offset - widthLeft;
            end = begin + wide;
            span = data.split(begin, end);
            return new Node(span);
        }

        /*
         * So now we know it's at least partially overlapping either left
         * and/or right. Do the left sub part first
         */

        if (offset < widthLeft) {
            // how many characters?
            across = widthLeft - offset;
            gauche = left.subset(offset, across);
            consumed = across;
        } else {
            gauche = null;
            consumed = 0;
        }

        /*
         * Now the right sub part. If we didn't get any left, then it's half
         * center and half right.
         */

        if (consumed == 0) {
            begin = offset - widthLeft;
            span = data.split(begin, widthCenter);

            consumed = widthCenter - begin;
            droit = right.subset(0, wide - consumed);
        }

        /*
         * Otherwise we did get some left.
         */

        else {
            across = wide - consumed;
            if (across <= widthCenter) {
                span = data.split(0, across);
                droit = null;
            } else {
                span = data;
                across -= widthCenter;
                droit = right.subset(0, across);
            }
        }

        return new Node(gauche, span, droit);
    }

    int getWordBoundaryBefore(final int offset) {
        final int widthLeft, widthCenter;
        final int result;
        int i, ch, previous;

        /*
         * Unlike the after case below, we _do_ have to check position zero.
         */

        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
        }

        /*
         * If it's on the left, then pass the search down, and we're done
         * here.
         */

        if (offset < widthLeft) {
            return left.getWordBoundaryBefore(offset);
        }

        /*
         * If the offset is on the right, pass it down. But if we don't find
         * it in the right sub node, we need to search here after all.
         */

        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }

        i = offset - widthLeft;

        if (right != null) {
            if (offset >= widthLeft + widthCenter) {
                result = right.getWordBoundaryBefore(offset - (widthCenter + widthLeft));
                if (result > -1) {
                    return widthLeft + widthCenter + result;
                }
            }
            i = widthCenter;
        }

        /*
         * Search here. We have to handle the corner cases of starting on a
         * space and starting at the end. The previous variable handles the i
         * + 1 case while allowing for the initial loop not needing this.
         */

        if (data != null) {
            previous = i;

            if (i == widthCenter) {
                i--;
            }

            while (i >= 0) {
                ch = data.getChar(i);

                if (isWhitespace(ch)) {
                    return widthLeft + previous;
                }

                previous = i;
                i--;
            }
        }

        if (left != null) {
            return left.getWordBoundaryBefore(widthLeft);
        }

        return -1;
    }

    int getWordBoundaryAfter(final int offset) {
        final int widthLeft, widthCenter;
        int result;
        int i, ch;

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

        if (left == null) {
            widthLeft = 0;
        } else {
            widthLeft = left.getWidth();
        }

        /*
         * If it's on the left, then pass the search down.
         */

        if (offset < widthLeft) {
            result = left.getWordBoundaryAfter(offset);
            if (result > -1) {
                return result;
            }
        }

        /*
         * Otherwise run through this node
         */

        if (data == null) {
            widthCenter = 0;
        } else {
            widthCenter = data.getWidth();
        }

        if (offset >= widthLeft + widthCenter) {
            result = right.getWordBoundaryAfter(offset - widthLeft - widthCenter);
            if (result > -1) {
                return widthLeft + widthCenter + result;
            }
        }

        if (offset > widthLeft) {
            i = offset - widthLeft;
        } else {
            i = 0;
        }
        while (i < widthCenter) {
            ch = data.getChar(i);
            if (isWhitespace(ch)) {
                return widthLeft + i;
            }
            i++;
        }

        /*
         * Still here? Ok, try going down the right side.
         */

        if (right != null) {
            if (i == widthCenter) {
                result = right.getWordBoundaryAfter(0);
                if (result > -1) {
                    return widthLeft + widthCenter + result;
                }
            } else {
                result = right.getWordBoundaryAfter(offset - (widthLeft + widthCenter));
                if (result > -1) {
                    return widthLeft + widthCenter + result;
                }
            }
        }

        // not found
        return -1;
    }

    Node rebalance() {
        int heightLeft, heightRight;

        if (left == null) {
            heightLeft = 0;
        } else {
            heightLeft = left.getHeight();
        }

        if (right == null) {
            heightRight = 0;
        } else {
            heightRight = right.getHeight();
        }

        if (heightLeft > heightRight + 2) {
            // rebalance left
            rebalanceLeft(); // FIXME
            return this;
        } else if (heightRight > heightLeft + 2) {
            // rebalance right
            return this;
        }

        return this;
    }

    /**
     * <pre>
     *       D
     *      / \
     *     B   E          B'
     *    / \           /   \
     *   A   C   ->    A     D'
     *  / \           / \   / \
     * X   Y         X   Y C'  E'
     * </pre>
     * 
     * Called on D, returns B'
     */
    private Node rebalanceLeft() {
        return null;
    }

    /*
     * Not a rebalance so much as repopulating
     */
    Node rebuild() {
        final ArrayList<Span> list;
        final int len;
        Span span;
        Node node;
        int i;

        list = new ArrayList<Span>();

        this.visit(new SpanVisitor() {
            public boolean visit(Span span) {
                list.add(span);
                return false;
            }
        });

        len = list.size();

        if (len == 1) {
            return this;
        }

        span = list.get(0);
        node = new Node(span);

        for (i = 1; i < len; i++) {
            span = list.get(i);
            node = node.append(span);
        }

        return node;
    }
}
