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
import org.gnome.gtk.TextTag;

public class ValidateBufferToMarkdownSerialization extends TestCaseGtk
{
    private static void insertMock(EditorWindow editor) {
        final TextBuffer buffer;
        final TextIter pointer;
        final TextTag italics;

        buffer = editor.getBuffer();
        italics = editor.getItalics();

        pointer = buffer.getIterStart();

        buffer.insert(pointer, "Hello ");
        buffer.insert(pointer, "world", italics);
    }

    public final void testExport() {
        EditorWindow editor;
        String text;

        editor = new EditorWindow();

        insertMock(editor);

        text = editor.extractToFile();

        assertEquals("Hello _world_", text);
    }
}
