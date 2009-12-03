/*
 * ValidateDocumentModified.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import quill.textbase.Change;
import quill.textbase.Common;
import quill.textbase.DataLayer;
import quill.textbase.Folio;
import quill.textbase.InsertTextualChange;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateDocumentModified extends IOTestCase
{
    private static final void insertThreeSpansIntoFirstSegment(final DataLayer data) {
        final TextChain chain;
        Span[] spans;
        int i, j;
        Span span;
        Change change;
        final Folio folio;
        final Series series;
        final Segment segment;

        folio = data.getActiveDocument();
        series = folio.get(0);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);
        chain = segment.getText();

        spans = new Span[] {
                createSpan("Hello", null), createSpan(" ", null), createSpan("World", Common.BOLD)
        };
        j = 0;
        for (i = 0; i < spans.length; i++) {
            span = spans[i];
            change = new InsertTextualChange(chain, j, span);
            data.apply(change);
            j += span.getWidth();
        }
    }

    public final void testChangeCausesModified() throws IOException {
        final DataLayer data;

        data = new DataLayer();
        data.createDocument();

        /*
         * An empty document is not modified.
         */

        assertFalse(data.isModified());

        /*
         * Modify, then undo back to empty.
         */

        insertThreeSpansIntoFirstSegment(data);

        assertTrue(data.isModified());
        data.undo();
        assertTrue(data.isModified());
        data.undo();
        assertTrue(data.isModified());
        data.undo();
        assertFalse(data.isModified());
    }

    public final void testSaveClearsModified() throws IOException {
        final DataLayer data;
        final ByteArrayOutputStream out;

        data = new DataLayer();
        data.createDocument();

        insertThreeSpansIntoFirstSegment(data);

        assertTrue(data.isModified());

        out = new ByteArrayOutputStream();
        data.saveDocument(out);
        assertFalse(data.isModified());

        /*
         * Now walk the undo/redo stack backwards and forwards past the new
         * (non-zero) save point to see if the modified behaviour holds
         */

        data.undo();
        assertTrue(data.isModified());

        data.redo();
        assertFalse(data.isModified());

        insertThreeSpansIntoFirstSegment(data);
        assertTrue(data.isModified());
        data.undo();
        data.undo();
        data.undo();
        assertFalse(data.isModified());
    }
}
