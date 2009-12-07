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

    private final int width;

    private final Node left;

    private final Span data;

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
}
