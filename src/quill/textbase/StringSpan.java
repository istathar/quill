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

/**
 * A Span multiple characters wide built by reusing an existing String.
 * 
 * @author Andrew Cowie
 */
public class StringSpan extends Span
{
    private final String data;

    /*
     * Relies on Span.createSpan() to ensure there are no surrogates in the
     * String; it's already checked it, and should only be calling this if
     * it's ok to do so. Sure, we could check it, but...
     */
    StringSpan(String str, Markup markup) {
        super(markup);
        data = str;
    }

    protected Span copy(Markup markup) {
        return new StringSpan(this.data, markup);
    }

    public String getText() {
        return data;
    }

    public int getWidth() {
        return data.length();
    }

    public int getChar(int position) {
        return data.charAt(position);
    }

    /*
     * The OpenJava implementation of substring() reuses the underlying
     * character array, so this is all good.
     */
    Span split(int begin, int end) {
        int width;

        width = end - begin;
        if (width == 0) {
            throw new IllegalArgumentException("zero width StringSpans not allowed");
        }

        if (width == 1) {
            return new CharacterSpan(data.charAt(begin), getMarkup());
        } else {
            return new StringSpan(data.substring(begin, end), getMarkup());
        }
    }
}
