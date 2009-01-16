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
package markerpen.textbase;

/**
 * Operations that can be applied to a TextStack.
 * 
 * @author Andrew Cowie
 */
public abstract class Change
{
    int offset;

    Chunk what;

    protected Change() {}

    /*
     * Interestingly (if somewhat by accident), by specifying Text here and
     * not TextStack the undo stack methods on TextStack are not visible,
     * which helps us resist the temptation to call its apply() from here :)
     */
    abstract void apply(Text text);

    abstract void undo(Text text);

    public int getOffset() {
        return offset;
    }

    public String getText() {
        return what.toString();
    }

    public int getLength() {
        return what.width;
    }
}
