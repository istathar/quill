/*
 * CharacterSpan.java
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
 * A Span comprising only a single Unicode character.
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
public class CharacterSpan extends Span
{
    final char ch;

    /**
     * We get the Text value so often that we will cache the conversion here,
     * and while doing so, cache references to the common Strings as well.
     */
    final String text;

    public CharacterSpan(String str, Markup markup) {
        super(markup);
        if (str.length() != 1) {
            throw new IllegalArgumentException();
        }
        this.ch = str.charAt(0);
        this.text = cache(str, ch);
    }

    public CharacterSpan(char ch, Markup markup) {
        super(markup);
        if (ch == 0) {
            throw new IllegalArgumentException();
        }
        if ((Character.isHighSurrogate(ch)) || (Character.isLowSurrogate(ch))) {
            throw new IllegalStateException();
        }
        this.ch = ch;
        this.text = lookup(ch);
    }

    private CharacterSpan(char ch, String str, Markup markup) {
        super(markup);
        this.ch = ch;
        this.text = str;
    }

    protected Span copy(Markup markup) {
        return new CharacterSpan(this.ch, this.text, markup);
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
    private static String lookup(char ch) {
        if (ch < 256) {
            if (cache[ch] == null) {
                cache[ch] = String.valueOf(ch);
            }
            return cache[ch];
        } else {
            return String.valueOf(ch).intern();
        }
    }

    private static String cache(String str, char ch) {
        if (ch < 256) {
            if (cache[ch] == null) {
                cache[ch] = str;
            }
            return str;
        } else {
            return str.intern();
        }
    }

    public String getText() {
        return text;
    }

    public int getChar(int position) {
        if (position != 0) {
            throw new IllegalArgumentException();
        }
        return ch;
    }

    public int getWidth() {
        return 1;
    }

    Span split(int begin, int end) {
        if ((begin != 0) || (end != 1)) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    Span split(int begin) {
        if (begin != 0) {
            throw new IllegalArgumentException();
        }
        return this;
    }

}
