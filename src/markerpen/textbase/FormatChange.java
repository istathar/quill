/*
 * FormatChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * Add format to a range of Text.
 * 
 * @author Andrew Cowie
 */
public class FormatChange extends Change
{
    Markup format;

    public FormatChange(int offset, Extract range, Markup format) {
        super(offset, range, applyMarkup(range, format));
        this.format = format;
    }

    private static Extract applyMarkup(Extract original, Markup format) {
        int i;
        Span s;
        Span[] range;

        range = new Span[original.size()];

        for (i = 0; i < original.size(); i++) {
            s = original.get(i);

            range[i] = s.applyMarkup(format);
        }

        return new Extract(range);
    }

    /**
     * Clear all format in the given range.
     */
    public FormatChange(int offset, Extract original) {
        super(offset, original, clearMarkup(original));
        this.format = null;
    }

    private static Extract clearMarkup(Extract original) {
        int i;
        Span s;
        Span[] range;

        range = new Span[original.size()];

        for (i = 0; i < original.size(); i++) {
            s = original.get(i);

            range[i] = s.copy(null);
        }

        return new Extract(range);
    }

    /*
     * Not using the returned Span[] for anything at the moment. What SHOULD
     * we be using it for?
     */
    /*
     * Doing clear() this way is cumbersome.
     */
    void apply(Text text) {
        text.delete(offset, removed.width);
        text.insert(offset, added.range);
    }

    /*
     * What about having cleared? We should be restoring the range, maybe?
     * Actually, this is wrong; if we're undoing we have the Span[] and should
     * be able to just "replace" with it.
     */
    void undo(Text text) {
        text.delete(offset, added.width);
        text.insert(offset, removed.range);
    }
}
