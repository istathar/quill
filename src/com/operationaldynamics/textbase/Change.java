/*
 * Change.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.textbase;

import java.io.File;

/**
 * Operations that can be applied to a TextStack.
 * 
 * @author Andrew Cowie
 */
public abstract class Change
{
    int offset;

    Chunk what;

    /*
     * Interestingly (if somewhat by accident), by specifying Text here and
     * not TextStack the undo stack methods on TextStack are not visible,
     * which helps us resist the temptation to call its apply() from here :)
     */
    abstract void apply(Text text);

    abstract void undo(Text text);
}

class Insertion extends Change
{
    Insertion(int offset, String what) {
        this.offset = offset;
        this.what = new Chunk(what);
    }

    void apply(Text text) {
        text.insert(offset, what);
    }

    void undo(Text text) {
        text.delete(offset, what.width);
    }
}

class Deletion extends Change
{
    int width;

    Deletion(int offset, int width) {
        this.offset = offset;
        this.width = width;
    }

    void apply(Text text) {
        this.what = text.delete(offset, width);
    }

    void undo(Text text) {
        if (what == null) {
            throw new IllegalStateException();
        }
        text.insert(offset, what);
    }
}

/**
 * Populate a Text, but don't hold a reference to the data being added.
 * 
 * @author Andrew Cowie
 */
class LoadFile extends Change
{
    /**
     * Load a String into a Text.
     */
    LoadFile(String text) {
        what = new Chunk(text);
    }

    public LoadFile(File file) {
    // TODO
    }

    void apply(Text text) {
        text.append(what);
    }

    void undo(Text text) {
        return;
    }
}
