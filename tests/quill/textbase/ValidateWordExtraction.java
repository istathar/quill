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

    public final void testBasic() {
        final TextChain chain;

        chain = new TextChain("This is a test of the emergency broadcast system.");
        assertEquals("test", chain.getWordAt(12));
    }
}
