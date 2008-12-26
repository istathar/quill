/*
 * DeletionChange.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package textbase;

public class DeleteChange extends Change
{
    int width;

    public DeleteChange(int offset, int width) {
        this.offset = offset;
        this.width = width;
    }

    final void apply(Text text) {
        super.what = text.delete(offset, width);
    }

    final void undo(Text text) {
        if (what == null) {
            throw new IllegalStateException();
        }
        text.insert(offset, what);
    }
}
