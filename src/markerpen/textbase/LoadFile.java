/*
 * LoadFile.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * Populate a Text, but don't hold a reference to the data being added. The
 * specified text will be appended. Not undoable.
 * 
 * @author Andrew Cowie
 */
public class LoadFile extends Change
{
    /**
     * Load a String into a Text.
     */
    LoadFile(String text) {
        what = new Chunk(text);
    }

    void apply(Text text) {
        text.append(what);
    }

    void undo(Text text) {
        return;
    }
}
