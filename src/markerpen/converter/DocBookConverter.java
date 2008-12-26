/*
 * DocBookConverter.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.converter;

import org.gnome.gtk.TextBuffer;

/**
 * Build a DocBook XOM tree equivalent to the data in our textbase, ready for
 * subsequent serialization (and thence saving to disk).
 * 
 * @author Andrew Cowie
 */
/*
 * A big reason to have this in its own package is so that we are using only
 * the public interface of our docbook module. Secondarily is that if other
 * output formats grow up, this will be the place their converters can live.
 */
public class DocBookConverter
{
    public static void buildTree(TextBuffer buffer) {

    }
}
