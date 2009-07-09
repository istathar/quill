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
        int i;
        final Segment twin;
        final TextChain original, twain;
        final Extract extract;
        final int width;

        for (i = 0; i < series.size(); i++) {
            if (series.get(i) == into) {
                break;
            }
        }

        /*
         * Grab the underlying TextChain, figure out an Extract with the
         * characters from the split point to its end, and then remove them.
         */

        original = into.getText();
        width = original.length() - offset;
        extract = original.extractRange(offset, width);

        original.delete(offset, width);

        /*
         * Now create a second Segment, place the extracted Spans there, and
         * then whack it into the Series.
         */

        twin = into.createSimilar();

        twain = new TextChain();
        twain.insert(0, extract.range);
        twin.setText(twain);

        i++;

        series.insert(i, twin);

        /*
         * Finally, insert our new Segment at the newly cleaved insertion
         * point.
         */

        series.insert(i, added);
    }

    protected void undo() {
        int i;

        for (i = 0; i < series.size(); i++) {
            if (series.get(i) == added) {
                break;
            }
        }

        series.delete(i);
    }
}
