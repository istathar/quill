/*
 * MarkerSpan.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * A special Span marking the position of a "marker"
 * 
 * @author Andrew Cowie
 */
public class MarkerSpan extends Span
{
    final String reference;

    public MarkerSpan(String reference, Markup markup) {
        super(markup);
        this.reference = reference;
    }

    protected Span copy(Markup markup) {
        return this; // ?
    }

    public String getText() {
        return reference;
    }

    public int getChar(int position) {
        if (position != 0) {
            throw new IllegalArgumentException();
        }
        return '†';
    }

    public int getWidth() {
        return 1;
    }

    // does this matter?
    Span split(int begin, int end) {
        if ((begin != 0) || (end != 1)) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    Span split(int begin) {
        if (begin != 0) {
            throw new IllegalArgumentException();
        }
        return this;
    }
}
