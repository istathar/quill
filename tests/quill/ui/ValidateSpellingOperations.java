/*
 * ValidateChangePropagation.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextTag;

import quill.textbase.Change;
import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.textbase.InsertTextualChange;
import quill.textbase.Segment;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.client.Quill.ui;
import static quill.textbase.Span.createSpan;

public class ValidateSpellingOperations extends GraphicalTestCase
{
    public final void testSpellingMarks() throws ValidityException, ParsingException, IOException {
        final DataLayer data;
        final Folio folio;
        final Segment segment;
        final TextChain chain;
        Change change;
        final Span span;
        final EditorTextView editor;
        final TextBuffer buffer;
        TextIter start, finish, pointer;
        String word;
        int i;
        TextTag[] tags;
        TextTag tag;

        data = new DataLayer();
        ui = new UserInterface(data);

        data.createDocument();
        folio = data.getActiveDocument();
        ui.displayDocument(folio);

        /*
         * Establish some starting text.
         */

        segment = folio.get(0).get(1);
        chain = segment.getText();
        span = createSpan("Test emrgency bro𝑎dcast system", null);

        change = new InsertTextualChange(chain, 0, span);
        ui.apply(change);

        assertEquals("Test emrgency bro𝑎dcast system", chain.toString());
        assertEquals(30, chain.length());

        /*
         * Initial state ok. Good. Now get a reference to the TextView, and
         * verify that we've got emrgency marked as misspelled, which is the
         * point of this test: loading should have been spell checked on
         * insert.
         */

        editor = (EditorTextView) findEditor(ui.primary.getChild());
        buffer = editor.getBuffer();
        pointer = buffer.getIterStart();

        start = buffer.getIterStart();
        finish = buffer.getIter(4);

        word = buffer.getText(start, finish, true);
        assertEquals("Test", word);

        pointer = buffer.getIterStart();

        for (i = 0; i < 5; i++) {
            tags = pointer.getTags();
            assertEquals(0, tags.length);
            pointer.forwardChar();
        }

        start = pointer.copy();
        finish = pointer.copy();
        finish.forwardChars(8);

        word = buffer.getText(start, finish, true);
        assertEquals("emrgency", word);

        for (i = 0; i < 8; i++) {
            tags = pointer.getTags();
            assertEquals(1, tags.length);
            tag = tags[0];
            assertSame(Format.spelling, tag);
            pointer.forwardChar();
        }

        pointer.forwardChar();

        start = pointer.copy();
        finish = pointer.copy();
        finish.forwardChars(9);

        word = buffer.getText(start, finish, true);
        assertEquals("bro𝑎dcast", word);

        for (i = 0; i < 9; i++) {
            tags = pointer.getTags();
            assertEquals(1, tags.length);
            tag = tags[0];
            assertSame(Format.spelling, tag);
            pointer.forwardChar();
        }

        pointer.forwardChar();

        start = pointer.copy();
        finish = pointer.copy();
        finish.forwardChars(6);

        assertTrue(finish.isEnd());
        word = buffer.getText(start, finish, true);
        assertEquals("system", word);

        for (i = 0; i < 6; i++) {
            tags = pointer.getTags();
            assertEquals(0, tags.length);
            pointer.forwardChar();
        }
        assertTrue(pointer.isEnd());
    }
}
