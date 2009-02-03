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
    InsertChange(int offset, String what) {
        super(offset, new Span[] {
            new StringSpan(what, null),
        });
    }

    public InsertChange(int offset, Span[] range) {
        super(offset, range);
    }

    final void apply(Text text) {
        text.insert(offset, range);
    }

    final void undo(Text text) {
        text.delete(offset, width);
    }
}
