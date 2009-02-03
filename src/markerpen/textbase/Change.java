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
    final int offset;

    final int width;

    Span[] range;

    protected Change(int offset, Span[] range) {
        int w;

        w = 0;

        for (Span s : range) {
            w += s.getWidth();
        }

        this.offset = offset;
        this.width = w;
        this.range = range;
    }

    protected Change(int offset, int width) {
        this.offset = offset;
        this.width = width;
    }

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

    /**
     * WARNING. Do not change the elements of the Span[]; if something changes
     * it that would be dangerous. So don't.
     */
    public Span[] getRange() {
        return range;
    }

    public int getLength() {
        return width;
    }
}
