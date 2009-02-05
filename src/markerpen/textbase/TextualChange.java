/*
 * TextualChange.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * A change to the character content of the TextChain. This can be an
 * insertion, a deletion, or a replacement (which is both simultaneously).
 * 
 * @author Andrew Cowie
 */
public class TextualChange extends Change
{
    /**
     * Insert a Span[] at offset
     */
    public TextualChange(int offset, Extract added) {
        super(offset, null, added);
    }

    /**
     * Replace the text between offset and width with a new Span[].
     */
    public TextualChange(int offset, Extract replaced, Extract added) {
        super(offset, replaced, added);
    }

    /**
     * Replace the text between offset and the width of replaced with the
     * given Span.
     */
    public TextualChange(int offset, Extract replaced, Span span) {
        super(offset, replaced, new Extract(span));
    }

    final void apply(Text text) {
        if (removed == null) {
            text.insert(offset, added.range);
        } else if (added == null) {
            text.delete(offset, removed.width);
        } else {
            text.delete(offset, removed.width);
            text.insert(offset, added.range);
        }
    }

    final void undo(Text text) {
        if (removed == null) {
            text.delete(offset, added.width);
        } else if (added == null) {
            text.insert(offset, removed.range);
        } else {
            text.delete(offset, added.width);
            text.insert(offset, removed.range);
        }
    }
}
