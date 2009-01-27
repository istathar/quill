/*
 * Pair.java
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
 * Wrapper touple representing the Pieces begining and ending a splice.
 * 
 * @author Andrew Cowie
 */
final class Pair
{
    final Piece one;

    final Piece two;

    Pair(Piece first, Piece second) {
        this.one = first;
        this.two = second;
    }

    public String toString() {
        return one + "\n" + two;
    }
}
