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

final class EmptyNode extends Node
{
    EmptyNode() {
        super();
    }

    public int getWidth() {
        return 0;
    }

    int getHeight() {
        return 0;
    }

    Node append(final Span addition) {
        return new LeafNode(addition);
    }

    boolean visitAll(SpanVisitor tourist) {
        return false;
    }

    boolean visitAll(CharacterVisitor tourist) {
        return false;
    }

    /**
     * Get a representation of this Node showing «» delimiters. Use for
     * debugging purposes only!
     */
    public String toString() {
        return "«»";
    }

    Span getSpanAt(int offset) {
        if (offset != 0) {
            throw new IndexOutOfBoundsException();
        }
        return null;
    }

    Node insertTreeAt(int offset, Node tree) {
        if (offset != 0) {
            throw new IndexOutOfBoundsException();
        }
        return tree;
    }

    Node subset(int offset, int wide) {
        if (offset != 0) {
            throw new IndexOutOfBoundsException("can't subset an empty node");
        }
        if (wide != 0) {
            throw new IndexOutOfBoundsException("can't subset an empty node");
        }
        return this;
    }

    boolean visitRange(final CharacterVisitor tourist, final int offset, final int wide) {
        return false;
    }

    int getWordBoundaryBefore(final int offset) {
        if (offset != 0) {
            throw new IndexOutOfBoundsException();
        }

        return -1;
    }

    int getWordBoundaryAfter(final int offset) {
        if (offset != 0) {
            throw new IndexOutOfBoundsException();
        }

        return -1;
    }

}
