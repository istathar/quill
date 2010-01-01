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

final class LeafNode extends Node
{
    /**
     * This Node's content: a Span (character+,Markup) which may be shared
     * around [notably due to cut and paste], and which in turn resuses its
     * data [in typing].
     */
    private final Span data;

    /**
     * Cached width of the content.
     */
    private final int width;

    LeafNode(Span span) {
        super();

        if (span == null) {
            throw new IllegalArgumentException();
        }

        width = span.getWidth();
        data = span;
    }

    public int getWidth() {
        return width;
    }

    int getHeight() {
        return 1;
    }

    Node append(final Span addition) {
        final Node omega;

        omega = new LeafNode(addition);
        return new BranchNode(this, omega);
    }

    boolean visitAll(final SpanVisitor tourist) {
        if (tourist.visit(data)) {
            return true;
        }
        return false;
    }

    boolean visitAll(final CharacterVisitor tourist) {
        final int I;
        int i, ch;
        Markup m;

        I = data.getWidth();
        m = data.getMarkup();
        for (i = 0; i < I; i++) {
            ch = data.getChar(i);
            if (tourist.visit(ch, m)) {
                return true;
            }
        }

        return false;
    }

    boolean visitRange(final CharacterVisitor tourist, final int offset, final int wide) {
        int i;
        int ch;
        Markup m;

        m = data.getMarkup();

        for (i = offset; i < offset + wide; i++) {
            ch = data.getChar(i);
            if (tourist.visit(ch, m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a representation of this Node showing Span content delimited by «».
     * Use for debugging purposes only!
     */
    public String toString() {
        final StringBuilder str;

        str = new StringBuilder();

        str.append("«");
        str.append(data.getText());
        str.append("»");

        return str.toString();
    }

    Span getSpanAt(int offset) {
        if (offset == width) {
            return null;
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        return data;
    }

    Node insertTreeAt(int offset, Node tree) {
        final Span before, after;
        final Node gauche, droit, node;

        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        if (offset == 0) {
            return new BranchNode(tree, this);
        }
        if (offset == width) {
            return new BranchNode(this, tree);
        }

        before = data.split(0, offset);
        after = data.split(offset);

        gauche = new LeafNode(before);
        droit = new LeafNode(after);

        node = new BranchNode(gauche, tree);
        return new BranchNode(node, droit);
    }

    Node subset(int offset, int wide) {
        final Span span;

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

        span = data.split(offset, offset + wide);
        return new LeafNode(span);
    }

    int getWordBoundaryBefore(final int offset) {
        int i, ch, previous;

        /*
         * Unlike the after case below, we _do_ have to check position zero.
         */

        if (offset > width) {
            throw new IndexOutOfBoundsException();
        }

        /*
         * Search here. We have to handle the corner cases of starting on a
         * space and starting at the end. The previous variable handles the i
         * + 1 case while allowing for the initial loop not needing this.
         */

        i = offset;
        previous = i;

        if (i == width) {
            i--;
        }

        while (i >= 0) {
            ch = data.getChar(i);

            if (isWhitespace(ch)) {
                return previous;
            }

            previous = i;
            i--;
        }

        return -1;
    }

    int getWordBoundaryAfter(final int offset) {
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

        /*
         * Otherwise run through this node
         */

        i = offset;

        while (i < width) {
            ch = data.getChar(i);
            if (isWhitespace(ch)) {
                return i;
            }
            i++;
        }

        /*
         * Still here? Ok, not found!
         */

        // not found
        return -1;
    }
}
