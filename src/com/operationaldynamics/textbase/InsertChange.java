/*
 * Insertion.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.textbase;

public class InsertChange extends Change
{
    public InsertChange(int offset, String what) {
        this.offset = offset;
        this.what = new Chunk(what);
    }

    final void apply(Text text) {
        text.insert(offset, what);
    }

    final void undo(Text text) {
        text.delete(offset, what.width);
    }
}
