/*
 * ValidateApplyUndoRedo.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package com.operationaldynamics.textbase;

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
        final TextStack text;
        Change change;

        text = new TextStack();

        change = new InsertChange(0, "Hello World");
        text.apply(change);
        assertEquals("Hello World", text.toString());

        change = new DeleteChange(5, 6);
        text.apply(change);
        assertEquals("Hello", text.toString());

        change = new DeleteChange(1, 3);
        text.apply(change);
        assertEquals("Ho", text.toString());

        /*
         * Now evaluate moving back and forth along the undo stack. redo at
         * the end should have no effect.
         */

        text.redo();
        assertEquals("Ho", text.toString());

        text.undo();
        assertEquals("Hello", text.toString());

        text.redo();
        assertEquals("Ho", text.toString());

        /*
         * Test going back to the beginning, and that overshooting is a no-op.
         */

        text.undo();
        text.undo();
        assertEquals("Hello World", text.toString());
        text.undo();
        assertEquals("", text.toString());
        text.undo();
        assertEquals("", text.toString());
        text.redo();
        assertEquals("Hello World", text.toString());

        text.redo();
        change = new InsertChange(5, " Santa Claus");
        text.apply(change);
        assertEquals("Hello Santa Claus", text.toString());

        /*
         * This new Change is a divergence and so creates a new branch of the
         * undo stack; thus redo should have no effect as the old branch will
         * have been discarded.
         */

        text.redo();
        assertEquals("Hello Santa Claus", text.toString());
    }
}
