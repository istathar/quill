/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
    public SplitStructuralChange(Segment into, int offset, Segment added) {
        super(into, offset, added);
    }

    protected void apply() {
        final Segment twin;
        final TextChain original, twain;
        Extract tree;
        final int i, width;
        final Series series;

        /*
         * Grab the underlying TextChain, figure out an Extract with the
         * characters from the split point to its end, and then remove them.
         */

        original = into.getText();
        width = original.length() - offset;

        if (offset == 0) {
            tree = null;
            i = index;
        } else if (width == 0) {
            tree = null;
            i = index + 1;
        } else {
            tree = original.extractRange(offset, width);
            original.delete(offset, width);
            i = index + 1;
        }

        /*
         * Now, if we're not appending but splitting, create a second Segment,
         * place the extracted Spans there, and then whack it into the Series.
         */

        series = into.getParent();

        if (tree != null) {
            twin = into.createSimilar();
            twain = new TextChain();
            twain.insert(0, tree);
            twin.setText(twain);

            series.insert(i, twin);
        }

        /*
         * Finally, insert our new Segment at the (newly cleaved if splitting)
         * insertion point.
         */

        series.insert(i, added);
    }

    protected void undo() {
        final Series series;
        TextChain original;
        int i;
        final Segment following;
        final TextChain first, second, third;
        final Node tree;

        series = into.getParent();

        /*
         * Was this a splice or an insert? If a splice, then get the index of
         * the second Segment. Then get the contents of the third Segment,
         * then empty it.
         */

        if (offset == 0) {
            i = 0;
        } else if (index + 2 == series.size()) {
            i = index + 1;
        } else {
            i = index + 2;

            following = series.get(i);
            third = following.getText();
            tree = third.extractAll();

            third.delete(0, tree.getWidth());

            first = into.getText();
            first.insert(offset, tree);

            series.delete(i);
            i--;
        }

        series.delete(i);
    }
}
