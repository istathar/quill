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
/*
 * The pieceAt() tests were written long after ValidateText, although that
 * code increasingly uses this code path. Leave it here; we created this when
 * we wore working out iterating by words.
 */
public class ValidateWordExtraction extends TestCase
{
    public final void testEmptyChain() {
        final TextChain chain;
        Piece piece;

        chain = new TextChain();

        piece = chain.pieceAt(0);
        assertNull(piece);
    }

    private static TextChain sampleData() {
        final TextChain result;

        result = new TextChain();
        result.append(Span.createSpan("One", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Two", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Three", null));
        result.append(Span.createSpan(' ', null));
        result.append(Span.createSpan("Four", null));

        return result;
    }

    public final void testSample() {
        final String expected;
        final TextChain chain;

        expected = "One Two Three Four";
        chain = sampleData();

        assertEquals(18, expected.length());
        assertEquals(18, chain.length());
        assertEquals(expected, chain.toString());
    }

    public final void testPieceAt() {
        final TextChain chain;
        Piece piece;

        chain = sampleData();

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

    public final void testEndPiece() {
        final TextChain chain;
        Piece piece;

        chain = sampleData();

        piece = chain.pieceAt(17);
        assertEquals("Four", piece.span.getText());

        // Not entirely sure about this. Should it be null?
        piece = chain.pieceAt(18);
        assertNotNull(piece);
        assertEquals("Four", piece.span.getText());
    }

    public final void testWordAt() {
        final TextChain chain;

        chain = new TextChain("This is a test of the emergency broadcast system.");
        assertSame(chain.first, chain.pieceAt(12));

        assertEquals("test", chain.getWordAt(12));
    }

}
