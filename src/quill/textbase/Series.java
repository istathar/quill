/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.textbase;

import java.util.List;

/**
 * A collection of Segments, comprising a visible section of a document. Like
 * other areas in textbase, is is a wrapper around an array. \
 * 
 * It also has information about what changed from the previous Series, to
 * facilitate undo and redo.
 * 
 * @author Andrew Cowie
 */
// immutable
public class Series
{
    private final Segment[] segments;

    public Series(List<Segment> segments) {
        final Segment[] result;

        result = new Segment[segments.size()];
        segments.toArray(result);

        this.segments = result;
    }

    Series(Segment[] segments) {
        this.segments = segments;
    }

    public int size() {
        return segments.length;
    }

    public Segment getSegment(int index) {
        return segments[index];
    }

    /**
     * Create a new Series by changing the Segment at position.
     */
    public Series update(int position, Segment segment) {
        final Segment[] original, replacement;

        original = this.segments;

        replacement = new Segment[original.length];

        System.arraycopy(original, 0, replacement, 0, position);
        replacement[position] = segment;
        System.arraycopy(original, position + 1, replacement, position + 1, original.length - position
                - 1);

        return new Series(replacement);
    }

    /**
     * Grow the Series by inserting the given Segment at position.
     */
    public Series insert(int position, Segment segment) {
        final Segment[] original, replacement;

        original = this.segments;

        replacement = new Segment[original.length + 1];

        System.arraycopy(original, 0, replacement, 0, position);
        replacement[position] = segment;
        System.arraycopy(original, position, replacement, position + 1, original.length - position);

        return new Series(replacement);
    }

    /**
     * Grow the Series by replacing the first Segment, inserting the added
     * Segment, and then following it with the second half of the original
     * Segment, third.
     */
    public Series splice(int position, Segment first, Segment added, Segment third) {
        final Segment[] original, replacement;

        if (third == null) {
            throw new AssertionError("Use insert() for the append case");
        }

        original = this.segments;
        replacement = new Segment[original.length + 2];

        System.arraycopy(original, 0, replacement, 0, position);
        replacement[position] = first;
        replacement[position + 1] = added;
        replacement[position + 2] = third;
        System.arraycopy(original, position, replacement, position + 2, original.length - position);

        return new Series(replacement);
    }

    /**
     * Remove the Segment at the given position.
     */
    public Series delete(int position) {
        final Segment[] original, replacement;

        original = this.segments;

        replacement = new Segment[original.length - 1];

        System.arraycopy(original, 0, replacement, 0, position);
        System.arraycopy(original, position + 1, replacement, position, original.length - position - 1);

        return new Series(replacement);
    }

    public int indexOf(Segment segment) {
        int i;

        for (i = 0; i < segments.length; i++) {
            if (segment == segments[i]) {
                return i;
            }
        }

        throw new IllegalArgumentException("\n" + "Segment not in this Series");
    }
}
