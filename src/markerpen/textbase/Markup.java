/*
 * Markup.java
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
 * Base class for indicating formatting and markup.
 * 
 * @author Andrew Cowie
 */
public abstract class Markup
{
    /*
     * This is for debugging; we can remove this down the track
     */
    private final String name;

    protected Markup(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    /*
     * TODO these shouldn't be public. Layering!
     */

    public static Markup[] applyMarkup(Markup[] original, Markup format) {
        final Markup[] replacement;
        final int len;

        if (original == null) {
            len = 0;
        } else {
            len = original.length;
        }

        if (contains(original, format)) {
            return original;
        }

        replacement = new Markup[len + 1];
        if (len > 0) {
            System.arraycopy(original, 0, replacement, 0, len);
        }

        replacement[len] = format;

        return replacement;
    }

    public static Markup[] removeMarkup(Markup[] original, Markup format) {
        final Markup[] replacement;
        final int len;
        int i;

        if (original == null) {
            return null;
        }

        if (!(contains(original, format))) {
            return original;
        }

        len = original.length - 1;

        if (len == 0) {
            return null;
        }

        replacement = new Markup[len];

        i = 0;
        for (Markup m : original) {
            if (m == format) {
                continue;
            }
            replacement[i++] = m;
        }

        return replacement;
    }

    /**
     * Does the given Markup[] contain the specified formatting?
     */
    static final boolean contains(Markup[] markup, Markup format) {
        if (markup == null) {
            return false;
        }
        for (Markup m : markup) {
            if (m == format) {
                return true;
            }
        }
        return false;
    }

}
