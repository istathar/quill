/*
 * Special.java
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
 * Special markup cases used to indicate Markers.
 * 
 * @author Andrew Cowie
 */
public class Special extends Markup
{
    private Special(String variant) {
        super("Special." + variant);
    }

    public static final Special NOTE = new Special("NOTE");

    public static final Special CITE = new Special("CITE");
}
