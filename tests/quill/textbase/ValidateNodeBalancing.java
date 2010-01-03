/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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

public class ValidateNodeBalancing extends TestCase
{
    /**
     * Case Left0:
     * 
     * <pre>
     *       C
     *      / \
     *     B  N4          B'
     *    / \           /   \
     *   A  N3   ->    A     C'
     *  / \           / \   / \
     * N1 N2         N1 N2 N3 N4
     * </pre>
     */
    private static Node sampleTreeLeft0() {
        Span span;
        final Node N1, N2, N3, N4, A, B, C;

        span = Span.createSpan("1", null);
        N1 = new LeafNode(span);

        span = Span.createSpan("2", null);
        N2 = new LeafNode(span);

        span = Span.createSpan("3", null);
        N3 = new LeafNode(span);

        span = Span.createSpan("4", null);
        N4 = new LeafNode(span);

        A = new BranchNode(N1, N2);
        B = new BranchNode(A, N3);
        C = new BranchNode(B, N4);

        return C;
    }

    private static void checkSampleData(Node tree) {
        tree.visit(new SpanVisitor() {
            private int i = 1;

            public boolean visit(Span span) {
                final String expected;
                final String actual;

                expected = Integer.toString(i);
                actual = span.getText();

                assertEquals(expected, actual);
                i++;

                return false;
            }
        });
    }

    /**
     * Test a left rebalancing, left bias
     */
    public final void testNodeRebalancingLeft0() {
        final Node tree, result;

        tree = sampleTreeLeft0();

        /*
         * Assert that our sample data is what we think it is
         */

        assertEquals(4, tree.getHeight());
        checkSampleData(tree);

        /*
         * Rebalance
         */

        result = tree.rebalance();

        /*
         * And find out if it did something useful!
         */

        assertEquals(3, result.getHeight());
        checkSampleData(result);
    }

    /**
     * Case Left1:
     * 
     * <pre>
     *       C
     *      / \
     *     B  N4          B'
     *    / \           /   \
     *   N1  A   ->    A'    C'
     *      / \       / \   / \
     *     N2 N3     N1 N2 N3 N4
     * </pre>
     */
    private static Node sampleTreeLeft1() {
        Span span;
        final Node N1, N2, N3, N4, A, B, C;

        span = Span.createSpan("1", null);
        N1 = new LeafNode(span);

        span = Span.createSpan("2", null);
        N2 = new LeafNode(span);

        span = Span.createSpan("3", null);
        N3 = new LeafNode(span);

        span = Span.createSpan("4", null);
        N4 = new LeafNode(span);

        A = new BranchNode(N2, N3);
        B = new BranchNode(N1, A);
        C = new BranchNode(B, N4);

        return C;
    }

    public final void testNodeRebalancingLeft1() {
        final Node tree, result;

        tree = sampleTreeLeft1();
        assertEquals(4, tree.getHeight());
        checkSampleData(tree);

        result = tree.rebalance();

        assertEquals(3, result.getHeight());
        checkSampleData(result);
    }
}
