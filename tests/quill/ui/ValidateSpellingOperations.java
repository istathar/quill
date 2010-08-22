/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.ui;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextTag;

import parchment.format.Manuscript;
import quill.client.Quill;
import quill.textbase.Change;
import quill.textbase.Common;
import quill.textbase.Folio;
import quill.textbase.InsertTextualChange;
import quill.textbase.Segment;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateSpellingOperations extends GraphicalTestCase
{
    public final void testSpellingMarks() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio;
        final PrimaryWindow primary;
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

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        primary = new PrimaryWindow();
        primary.displayDocument(folio);

        /*
         * Establish some starting text.
         */

        segment = folio.getSeries(0).get(1);
        chain = segment.getEntire();
        span = createSpan("Test emrgency bro𝑎dcast system", null);

        change = new InsertTextualChange(chain, 0, span);
        primary.apply(change);

        assertEquals("Test emrgency bro𝑎dcast system", chain.toString());
        assertEquals(30, chain.length());

        /*
         * Initial state ok. Good. Now get a reference to the TextView, and
         * verify that we've got emrgency marked as misspelled, which is the
         * point of this test: loading should have been spell checked on
         * insert.
         */

        editor = (EditorTextView) findEditor(primary.getChild());
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

    public final void testNotSpellingBasedOnMarkup() throws ValidityException, ParsingException,
            IOException {
        final Manuscript manuscript;
        final Folio folio;
        final UserInterface ui;
        final PrimaryWindow primary;
        final Span[] spans;
        final Segment segment;
        final TextChain chain;
        Change change;
        Span span;
        final EditorTextView editor;
        final TextBuffer buffer;
        TextIter start, finish, pointer;
        String word;
        int i, j;
        TextTag[] tags;
        TextTag tag;

        manuscript = new Manuscript();
        primary = new PrimaryWindow();

        folio = manuscript.createDocument();
        primary.displayDocument(folio);

        /*
         * Establish some starting text.
         */

        spans = new Span[] {
                createSpan("Use ", null),
                createSpan("dsmthng()", Common.FUNCTION),
                createSpan(" speek.", null),
        };

        segment = folio.getSeries(0).get(1);
        chain = segment.getEntire();

        for (i = 0, j = 0; i < spans.length; i++) {
            span = spans[i];
            change = new InsertTextualChange(chain, j, span);
            primary.apply(change);
            j += span.getWidth();
        }

        assertEquals("Use dsmthng() speek.", chain.toString());
        assertEquals(20, chain.length());

        /*
         * Verify that the thing we think is a spelling mistake actually is.
         */

        ui = Quill.getUserInterface();

        assertFalse(ui.dict.check("dsmthng"));

        /*
         * Initial state ok. Good. Now get a reference to the TextView, and
         * see what it has done.
         */

        editor = (EditorTextView) findEditor(primary.getChild());
        buffer = editor.getBuffer();
        pointer = buffer.getIterStart();

        start = buffer.getIterStart();
        finish = buffer.getIter(3);

        word = buffer.getText(start, finish, true);
        assertEquals("Use", word);

        pointer = buffer.getIterStart();

        for (i = 0; i < 4; i++) {
            tags = pointer.getTags();
            assertEquals(0, tags.length);
            pointer.forwardChar();
        }

        /*
         * Now make sure that printf() is NOT marked as mispelt. This
         * obviously replies on tight coupling between the Markup
         * Common.FUNCTION and TextTag Format.function.
         */

        start = pointer.copy();
        finish = pointer.copy();
        finish.forwardChars(9);

        word = buffer.getText(start, finish, true);

        assertEquals("dsmthng()", word);

        for (i = 0; i < 9; i++) {
            tags = pointer.getTags();
            assertEquals(1, tags.length);
            tag = tags[0];
            assertSame(Format.function, tag);
            pointer.forwardChar();
        }

        pointer.forwardChar();

        start = pointer.copy();
        finish = pointer.copy();
        finish.forwardChars(5);

        word = buffer.getText(start, finish, true);
        assertEquals("speek", word);

        for (i = 0; i < 5; i++) {
            tags = pointer.getTags();
            assertEquals(1, tags.length);
            tag = tags[0];
            assertSame(Format.spelling, tag);
            pointer.forwardChar();
        }

        assertFalse(pointer.isEnd());
        assertEquals('.', pointer.getChar());
        pointer.forwardChar();
        assertTrue(pointer.isEnd());
    }
}
