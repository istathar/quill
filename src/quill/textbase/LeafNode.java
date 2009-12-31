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
}
