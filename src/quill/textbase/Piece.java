/*
 * Piece.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
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
