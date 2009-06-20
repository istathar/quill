/*
 * InsertTextualChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

public class InsertTextualChange extends TextualChange
{
    /**
     * This is the usual case: you've created a single Span and want to insert
     * it.
     */
    public InsertTextualChange(TextChain chain, int offset, Span span) {
        super(chain, offset, null, new Extract(span));
    }

    /**
     * Alternately, you've been given a Range from somewhere and you want to
     * (re)insert it.
     */
    public InsertTextualChange(TextChain chain, int offset, Extract added) {
        super(chain, offset, null, added);
    }

    public void apply() {
        chain.insert(offset, added.range);
    }

    public void undo() {
        chain.delete(offset, added.width);
    }
}
