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
package quill.client;

import java.io.IOException;

import parchment.format.Manuscript;
import quill.textbase.Change;
import quill.textbase.Common;
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
    private static final void insertThreeSpansIntoFirstSegment(final Folio folio) {
        final TextChain chain;
        Span[] spans;
        int i, j;
        Span span;
        Change change;
        final Series series;
        final Segment segment;

        series = folio.getSeries(0);
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
        final Manuscript manuscript;

        manuscript = new Manuscript();
        manuscript.createDocument();

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
        final Manuscript manuscript;
        final Folio folio;

        manuscript = new Manuscript();
        manuscript.setFilename("UncertaintyPrinciple.parchment");
        folio = manuscript.createDocument();

        insertThreeSpansIntoFirstSegment(folio);

        assertTrue(data.isModified());

        manuscript.saveDocument(folio);
        assertFalse(data.isModified());

        /*
         * Now walk the undo/redo stack backwards and forwards past the new
         * (non-zero) save point to see if the modified behaviour holds.
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
