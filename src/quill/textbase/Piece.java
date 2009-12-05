/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
 * Linked list of Chunks and associated positional metadata which, chained
 * together, make up a Text.
 * 
 * @author Andrew Cowie
 */
class Piece
{
    Piece prev;

    Span span;

    Piece next;

    /**
     * A cache of the offset that the beginning of the Span is within the
     * parent TextChain.
     */
    int offset;

    Piece() {}

    /**
     * For debugging, only!
     */
    public String toString() {
        final StringBuilder str;
        Piece p;
        int i;

        str = new StringBuilder();

        p = this;
        i = 0;
        while (p != null) {
            p = p.prev;
            i++;
        }

        str.append("«");
        str.append(span.toString());
        str.append("» offset ");
        str.append(offset);
        str.append(" at the ");
        str.append(i);
        str.append(getOrdinalSuffix(i));
        str.append(" position");

        return str.toString();
    }

    private static String getOrdinalSuffix(int num) {
        final int dec;

        dec = num % 10;

        if (dec == 1) {
            return "st";
        } else if (dec == 2) {
            return "nd";
        } else if (dec == 3) {
            return "rd";
        } else {
            return "th";
        }
    }
}
