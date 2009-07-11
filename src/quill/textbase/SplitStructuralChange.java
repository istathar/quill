/*
 * SplitStructuralChange.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

public class SplitStructuralChange extends StructuralChange
{
    public SplitStructuralChange(Series series, Segment into, int offset, Segment added) {
        super(series, into, offset, added);
    }

    protected void apply() {
        final Segment twin;
        final TextChain original, twain;
        Extract extract;
        final int i, width;

        /*
         * Grab the underlying TextChain, figure out an Extract with the
         * characters from the split point to its end, and then remove them.
         */

        original = into.getText();
        width = original.length() - offset;

        if (offset == 0) {
            extract = null;
            i = index;
        } else if (width == 0) {
            extract = null;
            i = index + 1;
        } else {
            extract = original.extractRange(offset, width);
            original.delete(offset, width);
            i = index + 1;
        }

        /*
         * Now, if we're not appending but splitting, create a second Segment,
         * place the extracted Spans there, and then whack it into the Series.
         */

        if (extract != null) {
            twin = into.createSimilar();
            twain = new TextChain();
            twain.insert(0, extract.range);
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
        TextChain original;
        int i;
        final Segment following;
        final TextChain first, second, third;
        final Extract extract;

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
            i = index + 1;

            following = series.get(i + 1);
            third = following.getText();
            extract = third.extractAll();

            third.delete(0, extract.width);

            first = into.getText();
            first.insert(offset, extract.range);

            series.delete(i);
        }

        series.delete(i);
    }
}
