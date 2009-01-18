/*
 * StringSpan.java
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
 * A Span multiple characters wide built by reusing an existing String.
 * 
 * @author Andrew Cowie
 */
public class StringSpan extends Span
{
    private final String data;

    public StringSpan(String str, Markup[] markup) {
        super(markup);
        data = str;
    }

    public String getText() {
        return data;
    }

    public char getChar() {
        return 0;
    }
}
