/*
 * Piece.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * Linked list of Chunks and associated positional metadata which, chained
 * together, make up a Text.
 * 
 * @author Andrew Cowie
 */
class Piece
{
    Piece prev;

    Chunk chunk;

    Piece next;

    Piece() {}

    /**
     * For debugging, only!
     */
    public String toString() {
        StringBuilder str;
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
        str.append(chunk.toString());
        str.append("» at ");
        str.append(i);

        return str.toString();
    }
}
