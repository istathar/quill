/*
 * ValidateUnicode.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package quill.textbase;

import org.gnome.gtk.TextBuffer;

import quill.ui.GraphicalTestCase;

/**
 * Make sure textbase properly handles 2- and 3-byte characters.
 * 
 * @author Andrew Cowie
 */
/*
 * DANGER: Eclipse seems to be buggy at editing lines with >2 byte characters
 * in them. This file is UTF-8, so the content is correct. And the display in
 * Eclipse is correct. But if you try editing a line with the 3-byte n
 * character, Bad Things™ happen. Presumably Eclipse's editor is making the
 * assumption that String.length() == number of displayed characters.
 */
public class ValidateUnicode extends GraphicalTestCase
{
    public final void testLatin1Supplement() {
        final TextChain chain;

        chain = new TextChain("This character 'µ' is a micro symbol");
        assertEquals("This character '\u00b5' is a micro symbol", chain.toString());
    }

    public final void testMathematicalAlphanumericSymbols() {
        final TextChain chain1, chain2, chain3, chain4;

        chain1 = new TextChain("This character '𝑛' is an italic lower-case N");
        assertEquals("This character '\ud835\udc5b' is an italic lower-case N", chain1.toString());

        chain2 = new TextChain("This character '\ud835\udc5b' is an italic lower-case N");
        assertEquals("This character '𝑛' is an italic lower-case N", chain2.toString());

        chain3 = new TextChain("This character '𝑛' is an italic lower-case N");
        assertEquals("This character '𝑛' is an italic lower-case N", chain3.toString());

        chain4 = new TextChain("This character '\ud835\udc5b' is an italic lower-case N");
        assertEquals("This character '\ud835\udc5b' is an italic lower-case N", chain4.toString());
    }

    /*
     * Observe interations betwen UTF-8 .java sources and the Java Language
     * Specification, and use of multi-char UTF-16.
     */
    public final void testUnicdeSurrogates() {
        final String str;

        str = "1𝑛3";
        assertEquals(4, str.length()); // !?!

        assertEquals('1', str.charAt(0));
        assertEquals('1', str.codePointAt(0));

        assertEquals('\ud835', str.charAt(1));

        assertEquals(0x1d45b, str.codePointAt(1));
        assertTrue(Character.isLetter(0x1d45b));
        assertEquals(Character.LOWERCASE_LETTER, Character.getType(0x1d45b));

        /*
         * Now the freaky code unit stuff
         */

        assertTrue(Character.isHighSurrogate('\ud835'));
        assertTrue(!Character.isHighSurrogate('\udc5b'));
        assertEquals('\udc5b', str.charAt(2));
        // assertEquals(???, str.codePointAt(2));

        assertEquals('3', str.charAt(3));
        assertEquals('3', str.codePointAt(3));
    }

    public final void testOffsetCorrosion() {
        final TextBuffer buffer;
        final TextChain chain;
        final String str;

        str = "Cru𝑛ch";
        assertEquals(7, str.length());

        buffer = new TextBuffer();
        buffer.setText(str);
        assertEquals(6, buffer.getCharCount());

        chain = new TextChain(str);
        assertNotSame(str, chain.toString());
        assertEquals(str, chain.toString());
        assertEquals(6, chain.length());
    }
}
