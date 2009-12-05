/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
package quill.textbase;

import junit.framework.TestCase;

import static quill.textbase.Span.createSpan;

/**
 * Test applying Change instances, and that undo and redo of them works
 * satisfactorily.
 * 
 * @author Andrew Cowie
 */
/*
 * Assmes you've already done the Text tests.
 */
public class ValidateApplyUndoRedo extends TestCase
{
    public final void testInsertionAndDeletion() {
        final ChangeStack stack;
        final TextChain chain;
        Extract extract;
        Change change;

        stack = new ChangeStack();
        chain = new TextChain();

        change = new InsertTextualChange(chain, 0, new Extract(createSpan("Hello World", null)));
        stack.apply(change);
        assertEquals("Hello World", chain.toString());
        assertSame(change, stack.getCurrent());

        extract = chain.extractRange(5, 6);
        change = new DeleteTextualChange(chain, 5, extract);
        stack.apply(change);
        assertEquals("Hello", chain.toString());
        assertSame(change, stack.getCurrent());

        extract = chain.extractRange(1, 3);
        change = new DeleteTextualChange(chain, 1, extract);
        stack.apply(change);
        assertEquals("Ho", chain.toString());
        assertSame(change, stack.getCurrent());

        /*
         * Now evaluate moving back and forth along the undo stack. redo at
         * the end should have no effect.
         */

        stack.redo();
        assertEquals("Ho", chain.toString());
        assertSame(change, stack.getCurrent());

        stack.undo();
        assertEquals("Hello", chain.toString());

        stack.redo();
        assertEquals("Ho", chain.toString());

        /*
         * Test going back to the beginning, and that overshooting is a no-op.
         */

        stack.undo();
        stack.undo();
        assertEquals("Hello World", chain.toString());
        stack.undo();
        assertEquals("", chain.toString());
        stack.undo();
        assertEquals("", chain.toString());
        stack.redo();
        assertEquals("Hello World", chain.toString());

        stack.redo();
        change = new InsertTextualChange(chain, 5, new Extract(new Span[] {
            createSpan(" Santa Claus", null),
        }));
        stack.apply(change);
        assertEquals("Hello Santa Claus", chain.toString());
        assertSame(change, stack.getCurrent());

        /*
         * This new Change is a divergence and so creates a new branch of the
         * undo stack; thus redo should have no effect as the old branch will
         * have been discarded.
         */

        stack.redo();
        assertEquals("Hello Santa Claus", chain.toString());
    }
}
