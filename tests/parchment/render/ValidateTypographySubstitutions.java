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

import quill.ui.GraphicalTestCase;

import static parchment.render.LibertineTypography.toSmallCaps;
import static parchment.render.LibertineTypography.toSmallNumbers;

/**
 * Check the translation logic for small caps and small numbers.
 * 
 * @author Andrew Cowie
 */
public class ValidateTypographySubstitutions extends GraphicalTestCase
{
    /*
     * Interesting that unicode character literals really are their
     * hexidecimal equivalents. Which is as it should be, but hey, tests
     * confirm assumptions.
     */
    public final void testLibertineSmallCaps() {
        assertEquals(0xe053, toSmallCaps('C'));

        assertEquals(0xe05e, toSmallCaps('N'));
        assertEquals('\ue05e', toSmallCaps('N'));
        assertEquals('', toSmallCaps('N'));

        assertEquals(0xe027, toSmallNumbers('7'));
        assertEquals('', toSmallNumbers('7'));
    }

    private static void diagnose(int expected, int actual) {
        StringBuilder buf;

        buf = new StringBuilder(3);
        buf.appendCodePoint(expected);
        buf.append(' ');
        buf.appendCodePoint(actual);

        System.out.println(buf.toString());
    }

    public static void main(String[] args) {
        diagnose(0xe053, toSmallCaps('C'));
        diagnose('', toSmallCaps('N'));
        diagnose('', toSmallNumbers('7'));
    }
}
