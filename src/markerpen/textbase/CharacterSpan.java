/*
 * CharacterSpan.java
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
 * A Span comprising only a single Unicode character.
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
public class CharacterSpan extends Span
{
    final char ch;

    public CharacterSpan(char ch, Markup[] markup) {
        super(markup);
        this.ch = ch;
    }

    public String getText() {
        return Character.toString(ch);
    }

    public char getChar() {
        return ch;
    }
}
