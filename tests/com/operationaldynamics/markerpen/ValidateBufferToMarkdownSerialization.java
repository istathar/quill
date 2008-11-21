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
import org.gnome.gtk.TextMark;
import org.gnome.gtk.TextTag;

import static com.operationaldynamics.markerpen.Format.bold;
import static com.operationaldynamics.markerpen.Format.italics;
import static com.operationaldynamics.markerpen.Format.mono;
import static org.gnome.gtk.TextMark.LEFT;

public class ValidateBufferToMarkdownSerialization extends TestCaseGtk
{
    public final void testExportItalics() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "Hello ");
        buffer.insert(pointer, "world", italics);

        text = Serializer.extractToFile(buffer);

        assertEquals("Hello _world_", text);
    }

    public final void testExportBold() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "A ");
        buffer.insert(pointer, "brighter", bold);
        buffer.insert(pointer, " future");

        text = Serializer.extractToFile(buffer);

        assertEquals("A **brighter** future", text);
    }

    public final void testExportMono() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "Don't look in ");
        buffer.insert(pointer, "/etc/secret", mono);
        buffer.insert(pointer, " as it is secret!");

        text = Serializer.extractToFile(buffer);

        assertEquals("Don't look in `/etc/secret` as it is secret!", text);
    }

    public final void testExportSimultaneousMarkup() {
        final TextBuffer buffer;
        final TextIter pointer, start;
        final TextMark begin;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "Much ");

        begin = buffer.createMark(pointer, LEFT);
        buffer.insert(pointer, "emphasis");
        start = begin.getIter();
        buffer.applyTag(mono, start, pointer);
        buffer.applyTag(italics, start, pointer);
        buffer.applyTag(bold, start, pointer);

        buffer.insert(pointer, " needed");

        text = Serializer.extractToFile(buffer);

        assertEquals("Much _**`emphasis`**_ needed", text);
    }

    public final void testExportParagraphSpacing() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "One\nTwo");

        text = Serializer.extractToFile(buffer);

        assertEquals("One\n\nTwo", text);
    }

    public final void testLoadingFormatsInSequnce() {
        TextBuffer buffer;
        TextIter pointer;
        TextTag[] tags;

        /*
         * Applying different formats
         */

        buffer = Serializer.loadFile("**Hello** _world_");
        pointer = buffer.getIterStart();

        tags = pointer.getTags();
        assertEquals(1, tags.length);
        assertSame(bold, tags[0]);

        pointer.forwardChars(5);
        tags = pointer.getTags();
        assertEquals(0, tags.length);

        pointer.forwardChars(1);
        tags = pointer.getTags();
        assertEquals(1, tags.length);
        assertSame(italics, tags[0]);
    }

    public final void testLoadingFormatsInParallel() {
        TextBuffer buffer;
        TextIter pointer;
        TextTag[] tags;

        buffer = Serializer.loadFile("_**Important**_");
        pointer = buffer.getIterStart();

        tags = pointer.getTags();
        assertEquals(2, tags.length);
        assertNotSame(tags[0], tags[1]);
    }

    public final void testLoadingParagraphConversion() {
        TextBuffer buffer;
        TextIter pointer;
        int i;

        buffer = Serializer.loadFile("Hello\n\nworld");

        pointer = buffer.getIterStart();
        assertEquals("Hello\nworld", buffer.getText());

        i = 0;
        do {
            i++;
        } while (pointer.forwardLine());
        assertEquals(2, i);
    }

    public final void testLoadingBuggyInputTerminations() {
        TextBuffer buffer;
        TextIter pointer;
        TextTag[] tags;

        buffer = Serializer.loadFile("_**Impor\n\ntant**_");
        pointer = buffer.getIterStart();

        tags = pointer.getTags();
        assertEquals(2, tags.length);

        pointer.forwardLine();
        tags = pointer.getTags();
        assertEquals(0, tags.length);
    }

    public final void testLoadingMultipleBlankLines() {
        TextBuffer buffer;

        buffer = Serializer.loadFile("One\n\n\n\nThree");
        assertEquals("One\n\nThree", buffer.getText());

        /*
         * Three is a corner case, which we normalize. It will be four when
         * written out again.
         */

        buffer = Serializer.loadFile("One\n\n\nTwo");
        assertEquals("One\n\nTwo", buffer.getText());
    }

    public final void testRoundTripTwoBlankLines() {
        final TextBuffer before, after;
        final TextIter pointer;
        final String text;

        before = new TextBuffer();
        pointer = before.getIterStart();
        before.insert(pointer, "One\n\nTwo");

        text = Serializer.extractToFile(before);

        assertEquals("One\n\n\n\nTwo", text);

        after = Serializer.loadFile(text);
        assertEquals("One\n\nTwo", after.getText());
    }
}
