/*
 * ValidateWordExtraction.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package quill.textbase;

import junit.framework.TestCase;

/**
 * Extract words at locations in TextChains.
 * 
 * @author Andrew Cowie
 */
public class ValidateWordExtraction extends TestCase
{
    public final void testWordAt() {
        final TextChain chain;

        chain = new TextChain("This is a test of the emergency broadcast system.");

        assertEquals("test", chain.getWordAt(12));
        assertEquals("emergency", chain.getWordAt(25));
    }

    public final void testSingleWordsSinglePiece() {
        final String str;
        final TextChain chain;

        str = "Trance";

        chain = new TextChain(str);
        assertEquals(6, str.length());
        assertEquals(6, chain.length());

        assertEquals(null, chain.getWordAt(6));
    }

    public final void testWidthOne() {
        final String str;
        final TextChain chain;

        str = "A";

        chain = new TextChain(str);
        assertEquals(1, str.length());
        assertEquals(1, chain.length());

        assertEquals(null, chain.getWordAt(1));
        assertEquals("A", chain.getWordAt(0));
    }

    public final void testBoundaryConditions() {
        final TextChain chain;

        chain = new TextChain("This is a test of the emergency broadcast system");

        assertEquals("test", chain.getWordAt(10));
        assertEquals(null, chain.getWordAt(9));
        assertEquals("a", chain.getWordAt(8));
        assertEquals(null, chain.getWordAt(7));
        assertEquals("is", chain.getWordAt(6));
        assertEquals("is", chain.getWordAt(5));
        assertEquals(null, chain.getWordAt(4));
        assertEquals("This", chain.getWordAt(3));
        assertEquals("This", chain.getWordAt(2));
        assertEquals("This", chain.getWordAt(1));
        assertEquals("This", chain.getWordAt(0));

        assertEquals("test", chain.getWordAt(13));
        assertEquals(null, chain.getWordAt(14));
        assertEquals("of", chain.getWordAt(15));
        assertEquals("of", chain.getWordAt(16));
        assertEquals(null, chain.getWordAt(17));
        assertEquals("the", chain.getWordAt(18));

        assertEquals("system", chain.getWordAt(43));
    }

    public final void testWordsVersusPunctuation() {
        final String str;
        final TextChain chain;

        str = "Always the beginning: hello, world.";

        chain = new TextChain(str);
        assertEquals(35, str.length());
        assertEquals(35, chain.length());
        assertEquals('.', str.charAt(34));
        assertEquals('.', chain.toString().charAt(34));

        assertEquals("Always", chain.getWordAt(0));
        assertEquals("the", chain.getWordAt(7));
        assertEquals("beginning", chain.getWordAt(11));
        assertEquals("beginning", chain.getWordAt(19));
        assertEquals(null, chain.getWordAt(20));
        assertEquals(null, chain.getWordAt(21));
        assertEquals("hello", chain.getWordAt(22));
        assertEquals("hello", chain.getWordAt(26));
        assertEquals(null, chain.getWordAt(27));
        assertEquals(null, chain.getWordAt(28));
        assertEquals("world", chain.getWordAt(29));
        assertEquals("world", chain.getWordAt(33));
        assertEquals(null, chain.getWordAt(34));
        assertEquals(null, chain.getWordAt(35));
    }
}
