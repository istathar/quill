/*
 * Series.java
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
 * A collection of Segments, comprising a visible section of a document. Like
 * other areas in textbase, is is a wrapper around an array.
 * 
 * @author Andrew Cowie
 */
public class Series
{
    private final Segment[] segments;

    Series(Segment[] segments) {
        this.segments = segments;
    }

    public int size() {
        return segments.length;
    }

    public Segment get(int index) {
        return segments[index];
    }

    /**
     * Create a new Series with the given Segment inserted at position.
     */
    public Series insert(int position, Segment segment) {
        final Series result;

        result = new Series(new Segment[segments.length + 1]);

        System.arraycopy(this.segments, 0, result.segments, 0, position);
        result.segments[position] = segment;
        System.arraycopy(this.segments, position, result.segments, position + 1, this.segments.length
                - position);

        return result;
    }

    public Series delete(int position) {
        final Series result;

        result = new Series(new Segment[segments.length - 1]);

        System.arraycopy(this.segments, 0, result.segments, 0, position);
        System.arraycopy(this.segments, position + 1, result.segments, position, this.segments.length
                - position - 1);

        return result;
    }
}
