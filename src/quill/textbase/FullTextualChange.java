/*
 * FullTextualChange.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * A change to the character content of the TextChain. This can be an
 * insertion, a deletion, or a replacement (which is both simultaneously).
 * 
 * @author Andrew Cowie
 */
public class FullTextualChange extends TextualChange
{
    /**
     * Insert a Span[] at offset
     */
    public FullTextualChange(TextChain chain, int offset, Extract added) {
        super(chain, offset, null, added);
    }

    /**
     * Replace the text between offset and width with a new Span[].
     */
    public FullTextualChange(TextChain chain, int offset, Extract replaced, Extract added) {
        super(chain, offset, replaced, added);
    }

    /**
     * Replace the text between offset and the width of replaced with the
     * given Span.
     */
    public FullTextualChange(TextChain chain, int offset, Extract replaced, Span span) {
        super(chain, offset, replaced, new Extract(span));
    }

    protected void apply() {
        if (removed == null) {
            chain.insert(offset, added.range);
        } else if (added == null) {
            chain.delete(offset, removed.width);
        } else {
            chain.delete(offset, removed.width);
            chain.insert(offset, added.range);
        }
    }

    protected void undo() {
        if (removed == null) {
            chain.delete(offset, added.width);
        } else if (added == null) {
            chain.insert(offset, removed.range);
        } else {
            chain.delete(offset, added.width);
            chain.insert(offset, removed.range);
        }
    }
}
