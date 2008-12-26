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
package markdown;

import markdown.MarkdownSerializer;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Label;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TestCaseGtk;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextMark;
import org.gnome.gtk.TextTag;

import static markerpen.Format.bold;
import static markerpen.Format.hidden;
import static markerpen.Format.italics;
import static markerpen.Format.mono;

import static org.gnome.gtk.TextMark.LEFT;

public class ValidateBufferToMarkdownSerialization extends TestCaseGtk
{
    public final void testExportLastLineTerminated() {
        final TextBuffer buffer;
        final String text;

        buffer = new TextBuffer();
        buffer.setText("End");

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("End\n", text);
    }

    public final void testExportItalics() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "Hello ");
        buffer.insert(pointer, "world", italics);

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("Hello _world_\n", text);
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

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("A **brighter** future\n", text);
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

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("Don't look in `/etc/secret` as it is secret!\n", text);
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

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("Much _**`emphasis`**_ needed\n", text);
    }

    public final void testExportParagraphSpacing() {
        final TextBuffer buffer;
        final TextIter pointer;
        final String text;

        buffer = new TextBuffer();
        pointer = buffer.getIterStart();
        buffer.insert(pointer, "One\nTwo");

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("One\n\nTwo\n", text);
    }

    public final void testLoadingFormatsInSequnce() {
        TextBuffer buffer;
        TextIter pointer;
        TextTag[] tags;

        /*
         * Applying different formats
         */

        buffer = MarkdownSerializer.loadFile("**Hello** _world_");
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

        buffer = MarkdownSerializer.loadFile("_**Important**_");
        pointer = buffer.getIterStart();

        tags = pointer.getTags();
        assertEquals(2, tags.length);
        assertNotSame(tags[0], tags[1]);
    }

    public final void testLoadingParagraphConversion() {
        TextBuffer buffer;
        TextIter pointer;
        int i;

        buffer = MarkdownSerializer.loadFile("Hello\n\nworld");

        pointer = buffer.getIterStart();
        assertEquals("Hello\nworld", buffer.getText());

        i = 0;
        do {
            i++;
        } while (pointer.forwardLine());
        assertEquals(2, i);
    }

    public final void testLoadingBuggyInputTerminations() {
        final TextBuffer buffer;
        final TextIter pointer;
        TextTag[] tags;
        final String text;

        buffer = MarkdownSerializer.loadFile("_**Impor\n\ntant**_");
        pointer = buffer.getIterStart();

        tags = pointer.getTags();
        assertEquals(2, tags.length);

        pointer.forwardLine();
        tags = pointer.getTags();
        assertEquals(0, tags.length);

        text = MarkdownSerializer.extractToFile(buffer);
        assertEquals("_**Impor**_\n\ntant\n", text);
    }

    public final void testLoadingMultipleBlankLines() {
        TextBuffer buffer;

        buffer = MarkdownSerializer.loadFile("One\n\n\n\nThree");
        assertEquals("One\n\nThree", buffer.getText());

        /*
         * Three is a corner case, which we normalize. It will be four when
         * written out again.
         */

        buffer = MarkdownSerializer.loadFile("One\n\n\nTwo");
        assertEquals("One\n\nTwo", buffer.getText());
    }

    public final void testRoundTripTwoBlankLines() {
        final TextBuffer before, after;
        final String text;

        before = new TextBuffer();
        before.setText("One\n\nTwo");

        text = MarkdownSerializer.extractToFile(before);

        assertEquals("One\n\n\n\nTwo\n", text);

        after = MarkdownSerializer.loadFile(text);
        assertEquals("One\n\nTwo", after.getText());
    }

    public final void testLoadingImageMarkup() {
        TextBuffer buffer;
        TextIter pointer;
        char ch;
        buffer = MarkdownSerializer.loadFile("![Happy](smiley.png)\n");

        pointer = buffer.getIterStart();
        ch = pointer.getChar();
        assertEquals(TextBuffer.OBJECT_REPLACEMENT_CHARACTER, ch);
        assertEquals("smiley.png", buffer.getText());
        assertEquals("", buffer.getText(pointer, buffer.getIterEnd(), false));
    }

    public final void testExportImageMarkup() {
        final TextBuffer buffer;
        final TextIter pointer;
        final Pixbuf graphic;
        final String text;

        buffer = new TextBuffer();

        graphic = Gtk.renderIcon(new Label(""), Stock.MISSING_IMAGE, IconSize.BUTTON);

        pointer = buffer.getIterStart();
        buffer.insert(pointer, graphic);
        buffer.insert(pointer, "URL", new TextTag[] {
            hidden
        });

        /*
         * And now export
         */

        text = MarkdownSerializer.extractToFile(buffer);

        assertEquals("![text](URL)\n", text);
    }
}
