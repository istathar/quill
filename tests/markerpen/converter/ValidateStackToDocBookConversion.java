/*
 * ValidateBufferToMarkdownSerialization.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.converter;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import markerpen.textbase.TextStack;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import static markerpen.converter.DocBookConverter.parseTree;

public class ValidateStackToDocBookConversion extends TestCase
{
    public final void testLoadDocbook() throws IOException, ValidityException, ParsingException {
        final File source;
        final TextStack text;

        source = new File("tests/markerpen/converter/HelloWorld.xml");
        assertTrue(source.exists());

        text = parseTree(source);

        assertNotNull(text);
        assertEquals("Hello world", text.toString());
    }
}
