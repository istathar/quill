/*
 * Folio.java
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
 * A sequence of Series making up a document.
 * 
 * <p>
 * In the case of an article there will be exactly one Series. In the case of
 * a book, there will be a sequence of one or more Series.
 * 
 * @author Andrew Cowie
 */
public class Folio
{
    private Series[] collection;

    Folio(Series[] components) {
        this.collection = components;
    }

    public int size() {
        return collection.length;
    }

    public Series get(int index) {
        return collection[index];
    }
}
