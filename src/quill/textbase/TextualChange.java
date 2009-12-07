/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
 * Discrete operations that can be applied to a TextChain.
 * 
 * @author Andrew Cowie
 */
public abstract class TextualChange extends Change
{
    final TextChain chain;

    final int offset;

    final Extract removed;

    final Extract added;

    TextualChange(TextChain chain, int offset, Extract removed, Extract added) {
        super(chain.getEnclosingSegment());
        this.chain = chain;
        this.offset = offset;
        this.removed = removed;
        this.added = added;
    }

    public int getOffset() {
        return offset;
    }

    public Extract getRemoved() {
        return removed;
    }

    public Extract getAdded() {
        return added;
    }
}
