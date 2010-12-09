/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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
package parchment.render;

/**
 * Manually perform small caps and numeric typography with the beautiful
 * "Linux Libertine" font. This is necessary because Pango's
 * Variant.SMALL_CAPS doesn't actually work. Fortunately, the variant glyphs
 * are actually in the normal font, up in the Private Use area.
 * 
 * @author Andrew Cowie
 * @see parchment.quack.AcronymElement
 */
class LibertineTypography
{
    private LibertineTypography() {}

    /**
     * Take an <b>upper</b> case Latin character and translate it to the small
     * caps equivalent. Note that this is different than the usual mapping
     * done by the "Linux Libertine O C" font where 'A' -> 'A' and 'a' -> ''.
     * 
     * This implements our display algorithm for acronyms.
     */
    /*
     * Linux Libertine puts small caps variants for the main Latin characters
     * at U+E051 to U+E06A. It would be nice to have the other upper case
     * accented characters here, but a) at that point we really should be
     * changing fonts b) this is just a workaround, but best of all, c) who
     * ever heard of an acronym with accents?
     */
    static int toSmallCase(int ch) {
        if ((ch >= 'A') && (ch <= 'Z')) {
            return '\ue051' + (ch - 'A');
        } else if ((ch >= '0') && (ch <= '9')) {
            return '\ue020' + (ch - '0');
        } else {
            return ch;
        }
    }

    /**
     * Translate a normal lower case letter and make it small caps. This maps
     * 'a' -> '' and leaves 'A' -> 'A'.
     */
    static int toSmallCaps(int ch) {
        if ((ch >= 'a') && (ch <= 'z')) {
            return '\ue051' + (ch - 'a');
        } else {
            return ch;
        }
    }

    /**
     * Translate a normal digit and make it "lower case". This maps '4' -> ''
     * and '6' -> ''.
     */
    /*
     * Linux Libertine has the lower case numerals at U+E020 to U+E029.
     */
    static int toSmallNumbers(int ch) {
        if ((ch >= '0') && (ch <= '9')) {
            return '\ue020' + (ch - '0');
        } else {
            return ch;
        }
    }
}
