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

    int width;

    Span[] range;

    protected Change() {
        width = -1;
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

    // FIXME cache?
    public String getText() {
        final StringBuilder str;

        str = new StringBuilder();
        for (Span s : range) {
            str.append(s.getText());
        }

        return str.toString();
    }

    public int getLength() {
        int w;

        if (width == -1) {
            w = 0;

            for (Span s : range) {
                w += s.getWidth();
            }

            width = w;
        }

        return width;
    }
}
