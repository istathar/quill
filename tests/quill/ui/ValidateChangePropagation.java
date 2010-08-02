/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.Test;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;

import parchment.format.Manuscript;
import quill.client.Quill;
import quill.textbase.Change;
import quill.textbase.Common;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.FormatTextualChange;
import quill.textbase.InsertTextualChange;
import quill.textbase.Segment;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateChangePropagation extends GraphicalTestCase
{
    private final UserInterface ui;

    public ValidateChangePropagation() {
        ui = Quill.getUserInterface();
    }

    public final void testSetupBlank() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio1, folio2;

        manuscript = new Manuscript();

        folio1 = manuscript.createDocument();
        assertNotNull(folio1);

        folio2 = manuscript.createDocument();
        assertNotNull(folio2);

        assertNotSame(folio1, folio2);
    }

    public final void testInsertText() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio;
        final Segment segment;
        final TextChain chain;
        final Change change;
        final Span span;
        final OutputStream out;
        final String expected;

        manuscript = new Manuscript();
        ui = new UserInterface();

        folio = manuscript.createDocument();
        ui.displayDocument(manuscript, folio);

        segment = folio.getSeries(0).get(1);
        chain = segment.getText();
        span = createSpan('h', null);

        change = new InsertTextualChange(chain, 0, span);
        ui.apply(change);

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.2\">",
                "<text>",
                "h",
                "</text>",
                "</chapter>"
        });
        assertEquals(expected, out.toString());
    }

    public final void skipReplaceText() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio;
        final Segment segment;
        final TextChain chain;
        Change change;
        final Span span;
        OutputStream out;
        String expected;
        final EditorTextView editor;
        final TextBuffer buffer;
        TextIter start, end;

        manuscript = new Manuscript();

        data.createManuscript();
        folio = data.getActiveDocument();
        ui.displayDocument(folio);

        /*
         * Establish some starting text.
         */

        segment = folio.getSeries(0).get(1);
        chain = segment.getText();
        span = createSpan("This is a test of the emergency broadcast system", null);

        change = new InsertTextualChange(chain, 0, span);
        ui.apply(change);

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.2\">",
                "<text>",
                "This is a test of the emergency broadcast",
                "system",
                "</text>",
                "</chapter>"
        });
        assertEquals(expected, out.toString());

        /*
         * Now attempt to simulate the user replacing some of the text by
         * doing a recursive descent to find the editor then using the
         * interactive methods on its TextBuffer. Our code makes the
         * assumption that calls within the user-action pairs result from,
         * well, user action :) and so this causes the editor to raise and
         * apply Changes.
         */

        editor = (EditorTextView) findEditor(ui.primary.getChild());

        buffer = editor.getBuffer();
        start = buffer.getIter(9);
        end = buffer.getIter(21);
        buffer.selectRange(start, end);
        assertEquals(" test of the", buffer.getText(start, end, true));
        Test.cycleMainLoop();
        Test.sendKey(editor, Keyval.n, ModifierType.NONE);
        Test.cycleMainLoop();

        /*
         * We've still got the TextChain backing the Segment of this
         * EditorTextView, so first check to make sure it actually did what we
         * want (thereby passing this test fixture)
         */

        assertEquals("This is an emergency broadcast system", chain.toString());

        /*
         * Finally, just check the rest of the stack; the code was already
         * here.
         */

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.2\">",
                "<text>",
                "This is an emergency broadcast system",
                "</text>",
                "</chapter>"
        });
        assertEquals(expected, out.toString());
    }

    public final void skipFormatOnInsertion() throws ValidityException, ParsingException, IOException {
        final DataLayer data;
        final Folio folio;
        final Segment segment;
        final TextChain chain;
        Change change;
        Span span;
        Extract extract;
        OutputStream out;
        String expected;
        final EditorTextView editor;
        final TextBuffer buffer;
        TextIter start;

        data = new DataLayer();
        ui = new UserInterface(data);

        data.createManuscript();
        folio = data.getActiveDocument();
        ui.displayDocument(folio);

        /*
         * Establish some starting text.
         */

        segment = folio.getSeries(0).get(1);
        chain = segment.getText();

        span = createSpan("This is a test of the emergency broadcast system", null);
        change = new InsertTextualChange(chain, 0, span);
        ui.apply(change);

        span = createSpan("emergency", null);
        extract = Extract.create(span);
        change = new FormatTextualChange(chain, 22, extract, Common.ITALICS);
        ui.apply(change);

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.2\">",
                "<text>",
                "This is a test of the <italics>emergency</italics>",
                "broadcast system",
                "</text>",
                "</chapter>",
        });
        assertEquals(expected, out.toString());

        /*
         * Now attempt to simulate the user inserting text before. Moving the
         * cursor first is important, as it fires off the logic that sets
         * insertMarkup in EditorTextView.
         */

        editor = (EditorTextView) findEditor(ui.primary.getChild());

        buffer = editor.getBuffer();
        start = buffer.getIter(22);
        assertEquals('e', start.getChar());

        buffer.beginUserAction();
        buffer.placeCursor(start);
        buffer.endUserAction();

        buffer.beginUserAction();
        buffer.insertAtCursor("a");
        buffer.endUserAction();

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter xmlns=\"http://namespace.operationaldynamics.com/parchment/0.2\">",
                "<text>",
                "This is a test of the",
                "a<italics>emergency</italics> broadcast",
                "system",
                "</text>",
                "</chapter>"
        });
        assertEquals(expected, out.toString());

        /*
         * And now, the other side.
         */

        start = buffer.getIter(32);
        assertEquals(' ', start.getChar());

        buffer.beginUserAction();
        buffer.placeCursor(start);
        buffer.endUserAction();

        buffer.beginUserAction();
        buffer.insertAtCursor("a");
        buffer.endUserAction();

        out = new ByteArrayOutputStream();
        data.saveChapter(out);

        expected = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter version=\"5.0\" xmlns=\"http://docbook.org/ns/docbook\">",
                "<para>",
                "This is a test of the",
                "a<emphasis>emergencya</emphasis> broadcast",
                "system",
                "</para>",
                "</chapter>"
        });
        assertEquals(expected, out.toString());

    }
}
