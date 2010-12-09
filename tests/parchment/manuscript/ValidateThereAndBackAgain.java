/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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
package parchment.manuscript;

import java.io.File;
import java.io.IOException;

import parchment.manuscript.Chapter;
import parchment.manuscript.Manuscript;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.Series;

/**
 * <p>
 * I was watching <i>The Fellowship of the Ring</i> as I started writing this
 * test.
 * 
 * @author Andrew Cowie
 */
public class ValidateThereAndBackAgain extends IOTestCase
{
    private static void roundTripChapter(final String from) throws IOException, ValidityException,
            ParsingException, ImproperFilenameException {
        final Manuscript manuscript;
        final File source, target;
        final Chapter chapter;
        final Series series;
        final String msg;
        final String sum1, sum2;

        source = new File(from);
        assertTrue(source.exists());

        manuscript = new Manuscript();
        manuscript.setFilename("NonExistent.parchment"); // junk
        chapter = new Chapter(manuscript);
        chapter.setFilename(from);
        series = chapter.loadDocument();

        ensureDirectory("tmp/unittests/parchment/format/");
        manuscript.setFilename("tmp/unittests/parchment/format/ValidateThereAndBackAgain.parchment"); // junk
        chapter.setFilename("testRoundTrip.xml");
        target = new File(chapter.getFilename());

        chapter.saveDocument(series);

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

    public void testRoundTripChapter1() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        roundTripChapter("tests/SomeOfEverything.xml");
    }

    public void testRoundTripChapter2() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        roundTripChapter("tests/NotToBeOutdone.xml");
    }

    public void testRoundTripPart() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        roundTripChapter("tests/TheEnd.xml");
    }

    public void testRoundTripChapter3() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        roundTripChapter("tests/LastChapter.xml");
    }

    public static void main(String[] args) throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        int i;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/ExampleDocument.parchment");
        chapter = new Chapter(manuscript);
        chapter.setFilename("tests/SomeOfEverything.xml");
        series = chapter.loadDocument();

        for (i = 1; i <= 70; i++) {
            System.err.print(i / 10);
        }
        System.err.println();
        for (i = 1; i <= 70; i++) {
            System.err.print(i % 10);
        }
        System.err.println("\n");
        System.err.flush();

        chapter.saveDocument(series, System.out);
    }
}
