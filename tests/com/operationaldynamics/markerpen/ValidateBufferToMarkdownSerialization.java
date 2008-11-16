/*
 * ValidateBufferToMarkdownSerialization.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.markerpen;

import org.gnome.gtk.TestCaseGtk;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;

public class ValidateBufferToMarkdownSerialization extends TestCaseGtk
{
    private static void insertMock(TextBuffer buffer) {
        final TextIter pointer;

        pointer = buffer.getIterStart();

        buffer.insert(pointer, "Hello ");
        buffer.insert(pointer, "world", Format.italics);
    }

    public final void testExport() {
        final TextBuffer buffer;
        final String text;

        buffer = new TextBuffer();

        insertMock(buffer);

        text = Serializer.extractToFile(buffer);

        assertEquals("Hello _world_", text);
    }
}
