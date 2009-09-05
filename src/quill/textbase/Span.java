/*
 * Span.java
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
 * A contigiously formatted span of text.
 * 
 * In editing use, Spans will usually be one character wide, but as loaded
 * from an existing document, there will already be a String of the char data
 * and we thus greatly optimize by reusing these.
 * 
 * Formatting is handled by the reference to a Markup instance, most of which
 * are common and reusable. More complicated metadata, such as URLs, are
 * handled by per-use instances.
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
public class Span
{
    /**
     * The formatting applicable on this span. Will be null for the common
     * case of there being no particular markup on a paragraph of text.
     */
    private final Markup markup;

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
    public Span(String str, Markup markup) {
        final int len, width;
        int i, j;
        char ch;

        len = str.length();

        width = str.codePointCount(0, len);

        if ((len == 1) && (width == 1)) {
            this.data = cache(str.charAt(0));
        } else {
            this.data = str;
        }

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

        this.markup = markup;
    }

    public Span(char ch, Markup markup) {
        if ((Character.isHighSurrogate(ch)) || (Character.isLowSurrogate(ch))) {
            throw new IllegalArgumentException();
        }

        this.data = cache(ch);
        this.points = new int[1];
        this.points[0] = ch;
        this.markup = markup;
    }

    /*
     * This can be tuned, obviously.
     */

    private static String[] cache;

    static {
        cache = new String[256];
    }

    /**
     * Implement the same caching approach as is used in places like
     * Integer.valueOf() for low range common characters. In the case of
     * higher range numbers, we turn to the JVM's interning infrastructure for
     * Strings.
     */
    private static String cache(char ch) {
        if (ch < 256) {
            if (cache[ch] == null) {
                cache[ch] = String.valueOf(ch);
            }
            return cache[ch];
        } else {
            return String.valueOf(ch).intern();
        }
    }

    /**
     * Create a copy of this Span but with different Markup applying to it.
     */
    private Span(String data, int[] points, Markup markup) {
        this.data = data;
        this.points = points;
        this.markup = markup;
    }

    Span copy(Markup markup) {
        return new Span(this.data, this.points, markup);
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

    public Markup getMarkup() {
        return this.markup;
    }

    Span applyMarkup(Markup format) {
        if (format == markup) {
            return this;
        } else {
            return new Span(this.data, this.points, format);
        }
    }

    // TODO combine or revove!
    Span removeMarkup(Markup format) {
        if (format == this.markup) {
            return new Span(this.data, this.points, null);
        } else {
            return this;
        }
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

        return new Span(str.toString(), subset, this.markup);
    }

    /**
     * Get a new Span from <code>begin</code> to end.
     */
    Span split(int begin) {
        return this.split(begin, this.points.length);
    }

    /**
     * For debugging, only!
     */
    public String toString() {
        StringBuilder str;

        str = new StringBuilder();

        str.append('"');
        str.append(getText());
        str.append('"');

        if (this.markup == null) {
            str.append(", paragraph");
        } else {
            str.append(", ");
            str.append(this.markup.toString());
        }

        return str.toString();
    }
}
