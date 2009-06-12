/*
 * DeleteTextualChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

public class DeleteTextualChange extends TextualChange
{
    /**
     * While it would be nice to be able to say
     * "just remove between these two points", we need to have the Extract of
     * Spans that are going to be removed so we can restore them later if an
     * undo happens.
     */
    public DeleteTextualChange(int offset, Extract removed) {
        super(offset, removed, null);
    }

    final void apply(TextChain text) {
        text.delete(offset, removed.width);
    }

    final void undo(TextChain text) {
        text.insert(offset, removed.range);
    }
}
