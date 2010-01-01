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
    /*
     * TODO this is now ugly. The insert() case can do this itself, and
     * probably better, with knowledge of the tree shape?
     */
    static Node createNode(Node left, Span span, Node right) {
        Node node;

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
            return new BranchNode(left, right);
        }

        node = new BranchNode(left, new LeafNode(span));
        return new BranchNode(node, right);
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

    abstract Node append(final Span addition);

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
    abstract boolean visitRange(final CharacterVisitor tourist, final int offset, final int wide);

    public void visit(final CharacterVisitor tourist, final int offset, final int wide) {
        visitRange(tourist, offset, wide);
    }

    /*
     * Only used by tests.
     */
    abstract Span getSpanAt(final int offset);

    /*
     * FUTURE It may be that nothing in the production code actually uses
     * this! Given how single Span creation is the common case for the user
     * typing, if we fix up InsertTextChange to persist just Spans (rather
     * than automatically putting them into an Extract) or alternately stop
     * using Changes then we can probably use this for real.
     */
    Node insertSpanAt(final int offset, final Span addition) {
        final Node node;

        node = new LeafNode(addition);

        return insertTreeAt(offset, node);
    }

    abstract Node insertTreeAt(int offset, Node tree);

    /**
     * Get a subset of this tree from offset, wide characters in width.
     */
    abstract Node subset(int offset, int wide);

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
