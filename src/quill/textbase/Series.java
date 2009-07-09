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
    private Segment[] segments;

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
    void insert(int position, Segment segment) {
        final Segment[] result;

        result = new Segment[segments.length + 1];

        System.arraycopy(segments, 0, result, 0, position);
        result[position] = segment;
        System.arraycopy(segments, position, result, position + 1, segments.length - position);

        segments = result;
    }

    /**
     * Remove the Segment at the given position.
     */
    void delete(int position) {
        final Segment[] result;

        result = new Segment[segments.length - 1];

        System.arraycopy(segments, 0, result, 0, position);
        System.arraycopy(segments, position + 1, result, position, segments.length - position - 1);

        segments = result;
    }
}
