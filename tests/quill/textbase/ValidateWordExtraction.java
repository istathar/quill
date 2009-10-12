/*
 * ValidateWordExtraction.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

package quill.textbase;

import junit.framework.TestCase;

/**
 * Extract words at locations in TextChains.
 * 
 * @author Andrew Cowie
 */
public class ValidateWordExtraction extends TestCase
{
    public final void testPieceAt() {
        final TextChain chain;
        Piece piece;

        chain = new TextChain();
        chain.append(Span.createSpan("One", null));
        chain.append(Span.createSpan(' ', null));
        chain.append(Span.createSpan("Two", null));
        chain.append(Span.createSpan(' ', null));
        chain.append(Span.createSpan("Three", null));
        chain.append(Span.createSpan(' ', null));
        chain.append(Span.createSpan("Four", null));

        piece = chain.pieceAt(0);
        assertEquals("One", piece.span.getText());
        piece = chain.pieceAt(1);
        assertEquals("One", piece.span.getText());
        piece = chain.pieceAt(2);
        assertEquals("One", piece.span.getText());
        piece = chain.pieceAt(3);
        assertEquals(" ", piece.span.getText());
        piece = chain.pieceAt(4);
        assertEquals("Two", piece.span.getText());

        piece = chain.pieceAt(12);
        assertEquals("Three", piece.span.getText());
    }

    public final void testWordAt() {
        final TextChain chain;

        chain = new TextChain("This is a test of the emergency broadcast system.");
        assertSame(chain.first, chain.pieceAt(12));

        assertEquals("test", chain.getWordAt(12));
    }

}
