/*
 * ValidateApplyUndoRedo.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package quill.textbase;

import junit.framework.TestCase;

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

        change = new InsertTextualChange(chain, 0, new Extract(new StringSpan("Hello World", null)));
        stack.apply(change);
        assertEquals("Hello World", chain.toString());

        extract = chain.extractRange(5, 6);
        change = new DeleteTextualChange(chain, 5, extract);
        stack.apply(change);
        assertEquals("Hello", chain.toString());

        extract = chain.extractRange(1, 3);
        change = new DeleteTextualChange(chain, 1, extract);
        stack.apply(change);
        assertEquals("Ho", chain.toString());

        /*
         * Now evaluate moving back and forth along the undo stack. redo at
         * the end should have no effect.
         */

        stack.redo();
        assertEquals("Ho", chain.toString());

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
            new StringSpan(" Santa Claus", null),
        }));
        stack.apply(change);
        assertEquals("Hello Santa Claus", chain.toString());

        /*
         * This new Change is a divergence and so creates a new branch of the
         * undo stack; thus redo should have no effect as the old branch will
         * have been discarded.
         */

        stack.redo();
        assertEquals("Hello Santa Claus", chain.toString());
    }
}
