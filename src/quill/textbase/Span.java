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
public abstract class Span
{
    /**
     * The formatting applicable on this span. Will be null for the common
     * case of there being no particular markup on a paragraph of text.
     */
    private final Markup markup;

    Span(Markup markup) {
        this.markup = markup;
    }

    /**
     * Construct a new Span with the given String. If it is width 1, a cached
     * String reference will be used instead.
     */
    public static Span createSpan(String str, Markup markup) {
        final int len, width;

        len = str.length();

        if (len == 0) {
            throw new IllegalArgumentException("zero width Spans not allowed");
        }

        width = str.codePointCount(0, len);

        if ((len == 1) && (width == 1)) {
            return new CharacterSpan(str, markup);
        } else if (len != width) {
            return new UnicodeSpan(str, len, width, markup);
        } else {
            return new StringSpan(str, markup);
        }
    }

    public static Span createSpan(char ch, Markup markup) {
        return new CharacterSpan(ch, markup);
    }

    public static Span createMarker(String reference, Markup markup) {
        return new MarkerSpan(reference, markup);
    }

    abstract Span copy(Markup markup);

    /**
     * Get the character this Span represents, if it is only one character
     * wide. Position is from 0 to width.
     */
    public abstract int getChar(int position);

    /**
     * Get the text behind this Span for representation in the GUI. Since many
     * spans are only one character wide, use {@link #getChar(int) getChar()}
     * directly if building up Strings to pass populate Element bodies.
     */
    public abstract String getText();

    /**
     * Get the number of <b>characters</b> in this span.
     */
    public abstract int getWidth();

    public Markup getMarkup() {
        return this.markup;
    }

    Span applyMarkup(Markup format) {
        if (format == markup) {
            return this;
        } else {
            return this.copy(format);
        }
    }

    // TODO combine or revove!
    Span removeMarkup(Markup format) {
        if (format == this.markup) {
            return this.copy(null);
        } else {
            return this;
        }
    }

    /**
     * Create a new String by taking a subset of the existing one.
     * <code>begin</code> and <code>end</code> are character offsets.
     */
    abstract Span split(int begin, int end);

    /**
     * Get a new Span from <code>begin</code> to end.
     */
    Span split(int begin) {
        return split(begin, getWidth());
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

    public boolean equals(Object obj) {
        final Span other;
        final String mine, theirs;

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Span)) {
            return false;
        }

        other = (Span) obj;

        if (this.markup != other.markup) {
            return false;
        }

        mine = this.getText();
        theirs = other.getText();

        if (!(mine.equals(theirs))) {
            return false;
        }

        return true;
    }
}
