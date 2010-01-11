/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
package quill.quack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.textbase.DataLayer;

/**
 * <p>
 * I was watching <i>The Fellowship of the Ring</i> as I started writing this
 * test.
 * 
 * @author Andrew Cowie
 */
public class ValidateThereAndBackAgain extends IOTestCase
{
    public void testRoundTrip() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final File source, target;
        final FileOutputStream out;
        final String msg;
        final String sum1, sum2;

        source = new File("tests/ExampleProgram.xml");
        assertTrue(source.exists());

        data = new DataLayer();
        data.loadChapter(source.getPath());

        target = new File("tmp/unittests/quill/quack/ValidateThereAndBackAgain.xml");
        target.getParentFile().mkdirs();
        out = new FileOutputStream(target);
        data.saveChapter(out);

        /*
         * Now run an hashing algorithm over both files to figure out if
         * they're different.
         */

        sum1 = hash(source);
        sum2 = hash(target);

        msg = "\nLoading example source file\n" + source + "\nand round-tripping through to target\n"
                + target + "\nresulted in different output; hashes\n" + sum1 + " and\n" + sum2;

        assertTrue(msg, sum1.equals(sum2));
    }

    public static void main(String[] args) throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        int i;

        data = new DataLayer();
        data.loadChapter("tests/ExampleProgram.xml");

        for (i = 1; i <= 70; i++) {
            System.err.print(i / 10);
        }
        System.err.println();
        for (i = 1; i <= 70; i++) {
            System.err.print(i % 10);
        }
        System.err.println("\n");
        System.err.flush();

        data.saveChapter(System.out);
    }
}
