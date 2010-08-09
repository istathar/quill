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
package quill.textbase;

public class SplitStructuralChange extends StructuralChange
{
    private final Segment into;

    private final int offset;

    private final Segment added;

    public SplitStructuralChange(Series before, Segment into, int offset, Segment added) {
        super(before, computeSplit(before, into, offset, added));
        this.into = into;
        this.offset = offset;
        this.added = added;
    }

    /**
     * Work out the shape of the new Series, and create it, but do not apply
     * the changes to the underlying TextChains. That happens in apply().
     */
    /*
     * The usual do the work before the constructor trick
     */
    private static Series computeSplit(Series before, Segment into, int offset, Segment added) {
        final Segment twin;
        final TextChain original, twain;
        Extract tree;
        int i;
        final int width;
        Series series;

        i = before.indexOf(into);

        /*
         * Grab the underlying TextChain, figure out an Extract with the
         * characters from the split point to its end, and then remove them.
         */

        original = into.getText();
        width = original.length() - offset;

        if (offset == 0) {
            tree = null;
        } else if (width == 0) {
            tree = null;
            i++;
        } else {
            tree = original.extractRange(offset, width);
            i++;
        }

        /*
         * Now, if we're not appending but splitting, create a second Segment,
         * place the extracted Spans there, and then whack it into the Series.
         */

        if (tree != null) {
            twin = into.createSimilar();
            twain = new TextChain();
            twain.insert(0, tree);
            twin.setText(twain);

            series = before.insert(i, twin);
        } else {
            series = before;
        }

        /*
         * Finally, insert our new Segment at the (newly cleaved if splitting)
         * insertion point.
         */

        return series.insert(i, added);
    }

    protected void apply() {
        Series before, after;
        TextChain chain;
        int width;

        before = super.getBefore();
        after = super.getAfter();

        /*
         * There was a twain if there are 2 more segments, rather than 1 more.
         */

        if (before.size() + 2 == after.size()) {
            chain = into.getText();
            width = chain.length() - offset;
            chain.delete(offset, width);
        }
    }

    protected void undo() {
        Series before, after;
        Segment third;
        TextChain chain;
        int index;
        Extract all;

        before = super.getBefore();
        after = super.getAfter();

        /*
         * There was a twain if there are 2 more segments, rather than 1 more.
         */

        if (before.size() + 2 == after.size()) {
            index = after.indexOf(added);

            third = after.get(index + 1);
            chain = third.getText();
            all = chain.extractAll();

            chain = into.getText();
            chain.insert(offset, all);
        }
    }

    public Segment getInto() {
        return into;
    }

    /**
     * Offset into <code>into</code> that the split occured. Special cases are
     * <code>0</code> which indicates an prepend at the beginning of the
     * <code>into</code> Segment, and <code>-1</code> as the marker that an
     * append after happened.
     */
    public int getOffset() {
        return offset;
    }

    public Segment getAdded() {
        return added;
    }
}
