/*
 * Span.java
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
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
public abstract class Span
{
    Span prev;

    Span next;

    protected Span() {}

    /**
     * Get the text behind this Span for representation in the GUI. Since many
     * spans are only one character wide, access the char field directly if
     * building up Strings to pass populate Element bodies.
     */
    public abstract String getText();
}
