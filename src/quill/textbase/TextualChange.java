/*
 * TextualChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
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
