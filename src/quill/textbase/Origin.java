/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
