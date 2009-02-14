/*
 * StringSpan.java
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
 * A Span multiple characters wide built by reusing an existing String.
 * 
 * @author Andrew Cowie
 */
public class StringSpan extends Span
{
    private final String data;

    public StringSpan(String str, Markup markup) {
        super(markup);

        if (str.length() == 0) {
            throw new IllegalArgumentException();
        }

        data = str;
    }

    protected Span copy(Markup markup) {
        return new StringSpan(this.data, markup);
    }

    /*
     * The OpenJava implementation reuses the underlying character array, so
     * this is all good.
     */

    StringSpan(Span span, int begin) {
        super(span.getMarkup());
        data = span.getText().substring(begin);
    }

    StringSpan(Span span, int begin, int end) {
        super(span.getMarkup());
        data = span.getText().substring(begin, end);
    }

    public String getText() {
        return data;
    }

    public char getChar() {
        return 0;
    }

    public int getWidth() {
        return data.length();
    }
}
