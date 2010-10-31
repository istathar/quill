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
import parchment.format.Metadata;
import parchment.format.Stylesheet;
import quill.textbase.Folio;
import quill.textbase.Series;

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
        final Folio one, two, three, four;
        Folio folio;

        one = new Folio((Manuscript) null, (Chapter) null, (Series) null, (Stylesheet) null,
                (Metadata) null);
        two = new Folio((Manuscript) null, (Chapter) null, (Series) null, (Stylesheet) null,
                (Metadata) null);
        three = new Folio((Manuscript) null, (Chapter) null, (Series) null, (Stylesheet) null,
                (Metadata) null);
        four = new Folio((Manuscript) null, (Chapter) null, (Series) null, (Stylesheet) null,
                (Metadata) null);

        stack = new ChangeStack(one);
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

        folio = stack.redo();
        assertSame(three, folio);
        folio = stack.getCurrent();
        assertSame(three, folio);

        folio = stack.undo();
        assertSame(two, folio);
        folio = stack.getCurrent();
        assertSame(two, folio);

        folio = stack.redo();
        assertSame(three, folio);
        folio = stack.getCurrent();
        assertSame(three, folio);

        /*
         * Test going back to the beginning, and that overshooting is a no-op.
         */

        folio = stack.undo();
        folio = stack.undo();
        folio = stack.getCurrent();
        assertSame(one, folio);

        folio = stack.undo();
        assertSame(one, folio);
        folio = stack.getCurrent();
        assertSame(one, folio);

        folio = stack.undo();
        assertSame(one, folio);

        folio = stack.redo();
        assertSame(two, folio);

        stack.apply(four);
        folio = stack.getCurrent();
        assertSame(four, folio);

        /*
         * This new Change is a divergence and so creates a new branch of the
         * undo stack; thus redo should have no effect as the old branch will
         * have been discarded.
         */

        folio = stack.redo();
        assertSame(four, folio);
    }
}
