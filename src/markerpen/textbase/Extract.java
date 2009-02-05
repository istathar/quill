/*
 * Extract.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * Read-only wrapper around an array of Spans.
 * 
 * @author Andrew Cowie
 */
/*
 * The fields are package visible and that's ok because in here we know not to
 * change Span[], but if a Range escapes outside this package then the public
 * methods are available and this class is immutable.
 */
public class Extract
{
    final Span[] range;

    final int width;

    public static final Extract EMPTY;

    static {
        EMPTY = new Extract();
    }

    private Extract() {
        this.width = 0;
        this.range = null;
    }

    Extract(Span[] range) {
        int w;

        w = 0;

        for (Span s : range) {
            w += s.getWidth();
        }

        this.width = w;
        this.range = range;
    }

    Extract(Span span) {
        this.width = span.getWidth();
        this.range = new Span[] {
            span
        };
    }

    /**
     * Get the number of Spans in this Range.
     */
    public int size() {
        return range.length;
    }

    /**
     * Get the Span at index. Don't exceed {@link #size()} - 1.
     */
    public Span get(int index) {
        return range[index];
    }

    /**
     * Get the width, in characeters, that the Spans in this Range represent.
     */
    public int getWidth() {
        return width;
    }
}
