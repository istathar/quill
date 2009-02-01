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
package markerpen.textbase;

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
     * case of there being no particular markup on normal text.
     */
    /*
     * WARNING Our use of these arrays is to allocate them once and then reuse
     * them, but since Java arrays are mutable we will have to change to a
     * wrapper Object if the array setters make it outside this package.
     */
    private final Markup[] markup;

    protected Span(Markup[] markup) {
        this.markup = markup;
    }

    protected abstract Span copy(Markup[] markup);

    /**
     * Get the char this Span represents, if it is only one character wide.
     * Will return 0x0 if the Span is wider than a single character.
     */
    public abstract char getChar();

    /**
     * Get the text behind this Span for representation in the GUI. Since many
     * spans are only one character wide, use {@link #getChar()} directly if
     * building up Strings to pass populate Element bodies.
     */
    public abstract String getText();

    public abstract int getWidth();

    public Markup[] getMarkup() {
        return markup;
    }

    Span applyMarkup(Markup format) {
        final Markup[] replacement;

        replacement = Markup.applyMarkup(markup, format);

        if (replacement == markup) {
            return this;
        } else {
            return this.copy(replacement);
        }
    }

    Span removeMarkup(Markup format) {
        final Markup[] replacement;

        replacement = Markup.removeMarkup(markup, format);

        if (replacement == markup) {
            return this;
        } else {
            return this.copy(replacement);
        }
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

        if (markup == null) {
            str.append(" no markup");
        } else {
            str.append(" with markups ");

            for (int i = 0; i < markup.length; i++) {
                str.append(markup[i]);
                if (i > 0) {
                    str.append(", ");
                }
            }
        }

        return str.toString();
    }
}
