/*
 * UnicodeSpan.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * A contigiously formatted span of unicode text.
 * 
 * @author Andrew Cowie
 */
public class UnicodeSpan extends Span
{
    /**
     * Cached copy of the String this Span came from.
     */
    private final String data;

    /**
     * Because Strings can contain Unicode surrogate pairs, we also need the
     * backing data in code point form.
     */
    final int[] points;

    /**
     * Construct a new Span with the given String. If it is width 1, a cached
     * String reference will be used instead.
     */
    UnicodeSpan(String str, int width, Markup markup) {
        super(markup);

        final int len;
        int i, j;
        char ch;

        len = str.length();

        this.data = str;

        this.points = new int[width];

        j = 0;
        for (i = 0; i < len; i++) {
            ch = str.charAt(i);

            if (Character.isHighSurrogate(ch)) {
                this.points[j] = str.codePointAt(i);
                i++;
            } else if (Character.isLowSurrogate(ch)) {
                throw new IllegalStateException();
            } else {
                this.points[j] = ch;
            }
            j++;
        }
    }

    /**
     * Create a copy of this Span but with different Markup applying to it.
     */
    private UnicodeSpan(String data, int[] points, Markup markup) {
        super(markup);
        this.data = data;
        this.points = points;
    }

    Span copy(Markup markup) {
        return new UnicodeSpan(this.data, this.points, markup);
    }

    /**
     * Get the character this Span represents, if it is only one character
     * wide. Position is from 0 to width.
     */
    public int getChar(int position) {
        return this.points[position];
    }

    /**
     * Get the text behind this Span for representation in the GUI. Since many
     * spans are only one character wide, use {@link #getChar(int) getChar()}
     * directly if building up Strings to pass populate Element bodies.
     */
    public String getText() {
        return this.data;
    }

    /**
     * Get the number of <b>characters</b> in this span.
     */
    public int getWidth() {
        return this.points.length;
    }

    /**
     * Create a new String by taking a subset of the existing one.
     * <code>begin</code> and <code>end</code> are character offsets.
     */
    Span split(int begin, int end) {
        final int[] subset;
        final int width;
        final StringBuilder str;
        int i;

        width = end - begin;
        subset = new int[width];

        System.arraycopy(this.points, begin, subset, 0, width);

        /*
         * We're not done, however: we need to create a new Java String to be
         * cached by the Span. I wonder if this will be a hot spot, but I'm
         * not sure what a better approach will be.
         */

        str = new StringBuilder();

        for (i = 0; i < subset.length; i++) {
            str.appendCodePoint(subset[i]);
        }

        return new UnicodeSpan(str.toString(), subset, this.getMarkup());
    }
}
