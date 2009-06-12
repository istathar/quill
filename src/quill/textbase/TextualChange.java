/*
 * TextualChange.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
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
public abstract class TextualChange
{
    final int offset;

    final Extract removed;

    final Extract added;

    TextualChange(int offset, Extract removed, Extract added) {
        this.offset = offset;
        this.removed = removed;
        this.added = added;
    }

    /*
     * Interestingly (if somewhat by accident), by specifying Text here and
     * not TextStack the undo stack methods on TextStack are not visible,
     * which helps us resist the temptation to call its apply() from here :)
     */
    abstract void apply(TextChain text);

    abstract void undo(TextChain text);

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
