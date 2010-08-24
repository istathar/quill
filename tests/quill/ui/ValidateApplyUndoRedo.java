/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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

import junit.framework.TestCase;
import parchment.format.Chapter;
import parchment.format.Manuscript;
import quill.textbase.Folio;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

/**
 * Test adding Folio instances to a ChangeStack, and that undo and redo of
 * them works satisfactorily.
 * 
 * @author Andrew Cowie
 */
public class ValidateApplyUndoRedo extends TestCase
{
    public final void testInsertionAndDeletion() {
        final ChangeStack stack;
        final TextChain chain;
        final Span span;
        final Folio one, two, three, four;
        Folio folio;

        stack = new ChangeStack();
        chain = new TextChain();
        span = createSpan("Hello World", null);
        chain.append(span);

        folio = stack.getCurrent();
        assertNull(folio);

        one = new Folio((Manuscript) null, (Chapter) null, (Series) null);
        two = new Folio((Manuscript) null, (Chapter) null, (Series) null);
        three = new Folio((Manuscript) null, (Chapter) null, (Series) null);
        four = new Folio((Manuscript) null, (Chapter) null, (Series) null);

        stack.apply(one);
        folio = stack.getCurrent();
        assertSame(one, folio);

        stack.apply(two);
        folio = stack.getCurrent();
        assertSame(two, folio);

        stack.apply(three);
        folio = stack.getCurrent();
        assertSame(three, folio);

        /*
         * Now evaluate moving back and forth along the undo stack. redo at
         * the end should have no effect.
         */

        stack.redo();
        folio = stack.getCurrent();
        assertSame(three, folio);

        stack.undo();
        folio = stack.getCurrent();
        assertSame(two, folio);

        stack.redo();
        folio = stack.getCurrent();
        assertSame(three, folio);

        /*
         * Test going back to the beginning, and that overshooting is a no-op.
         */

        stack.undo();
        stack.undo();
        folio = stack.getCurrent();
        assertSame(one, folio);

        stack.undo();
        folio = stack.getCurrent();
        assertNull(folio);

        stack.undo();
        folio = stack.getCurrent();
        assertNull(folio);
        stack.redo();
        folio = stack.getCurrent();
        assertSame(one, folio);
        stack.redo();
        folio = stack.getCurrent();
        assertSame(two, folio);

        stack.apply(four);
        folio = stack.getCurrent();
        assertSame(four, folio);

        /*
         * This new Change is a divergence and so creates a new branch of the
         * undo stack; thus redo should have no effect as the old branch will
         * have been discarded.
         */

        stack.redo();
        folio = stack.getCurrent();
        assertSame(four, folio);
    }
}
