/*
 * FormatChange.java
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
 * Add format to a range of Text.
 * 
 * @author Andrew Cowie
 */
public class FormatChange extends Change
{
    int width;

    Markup format;

    boolean additive;

    public FormatChange(int offset, int width, Markup format) {
        super();
        this.offset = offset;
        this.width = width;
        this.format = format;
        this.additive = true;
    }

    /*
     * Not using the returned Span[] for anything at the moment. What SHOULD
     * we be using it for?
     */
    void apply(Text text) {
        super.range = text.format(offset, width, format, additive);
    }

    void undo(Text text) {
        if (range == null) {
            throw new IllegalStateException();
        }
        text.format(offset, width, format, !additive);
    }
}
