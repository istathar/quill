/*
 * InsertChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

public class InsertChange extends Change
{
    /**
     * This is the usual case: you've created a single Span and want to insert
     * it.
     */
    public InsertChange(int offset, Span span) {
        super(offset, null, new Extract(span));
    }

    /**
     * Alternately, you've been given a Range from somewhere and you want to
     * (re)insert it.
     */
    public InsertChange(int offset, Extract added) {
        super(offset, null, added);
    }

    final void apply(Text text) {
        text.insert(offset, added.range);
    }

    final void undo(Text text) {
        text.delete(offset, added.width);
    }
}
