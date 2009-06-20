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
package quill.textbase;

/**
 * Add format to a range of Text.
 * 
 * @author Andrew Cowie
 */
public class FormatTextualChange extends TextualChange
{
    Markup format;

    /**
     * Toggle format in the given range. This means applying it, unless the
     * first Span in the Extract is that format, in which case toss it.
     */
    public FormatTextualChange(TextChain chain, int offset, Extract range, Markup format) {
        super(chain, offset, range, toggleMarkup(range, format));
        this.format = format;
    }

    private static Extract toggleMarkup(Extract original, Markup format) {
        final Markup markup;

        markup = original.range[0].getMarkup();

        // TODO change to handle instances rather than singletons
        if (markup == null) {
            return applyMarkup(original, format);
        } else if (markup == format) {
            return removeMarkup(original, format);
        } else {
            return applyMarkup(original, format);
        }
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

    private static Extract removeMarkup(Extract original, Markup format) {
        int i;
        Span s;
        Span[] range;

        range = new Span[original.size()];

        for (i = 0; i < original.size(); i++) {
            s = original.get(i);

            range[i] = s.removeMarkup(format);
        }

        return new Extract(range);
    }

    /**
     * Clear all format in the given range.
     */
    public FormatTextualChange(TextChain chain, int offset, Extract original) {
        super(chain, offset, original, clearMarkup(original));
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
    protected void apply() {
        chain.delete(offset, removed.width);
        chain.insert(offset, added.range);
    }

    /*
     * What about having cleared? We should be restoring the range, maybe?
     * Actually, this is wrong; if we're undoing we have the Span[] and should
     * be able to just "replace" with it.
     */
    protected void undo() {
        chain.delete(offset, added.width);
        chain.insert(offset, removed.range);
    }
}
