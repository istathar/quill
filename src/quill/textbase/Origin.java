/*
 * Origin.java
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
 * A cursor location. Is used as a key to sort out where an
 * {@link parchment.render.Area Area} came from, and which
 * {@link parchment.render.Page Page} it's on.
 * 
 * @author Andrew Cowie
 */
public final class Origin implements Comparable<Origin>
{
    private final int position;

    private final int offset;

    /**
     * The Segment and cursor position in that Segment that an Area is to be
     * rendered from.
     */
    public Origin(final int position, final int offset) {
        this.position = position;
        this.offset = offset;
    }

    public int compareTo(Origin other) {
        if (this.position < other.position) {
            return -1;
        } else if (this.position > other.position) {
            return 1;
        } else {
            if (this.offset < other.offset) {
                return -1;
            } else if (this.offset > other.offset) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean equals(Object obj) {
        final Origin other;
        if (obj instanceof Origin) {
            other = (Origin) obj;
        } else {
            return false;
        }

        if ((this.position == other.position) && (this.offset == other.offset)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "(Segment: #" + position + ", offset: " + offset + ")";
    }
}
