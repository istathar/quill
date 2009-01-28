/*
 * DeleteChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

public class DeleteChange extends Change
{
    public DeleteChange(int offset, int width) {
        this.offset = offset;
        super.width = width;
    }

    final void apply(Text text) {
        super.range = text.delete(offset, width);
    }

    final void undo(Text text) {
        if (range == null) {
            throw new IllegalStateException();
        }
        text.insert(offset, range);
    }
}
