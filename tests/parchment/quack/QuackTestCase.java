/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
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
package parchment.quack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.manuscript.Chapter;
import parchment.manuscript.Manuscript;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.Component;

/**
 * Tests to round-trip a Quack Schema chapter. Use as follows:
 * 
 * <pre>
 * public final void testFeatureSix() {
 *     component = super.loadDocument(&quot;tests/parchment/quack/ImageWithCaption.xml&quot;);
 * 
 *     // do tests, as you wish.
 * 
 *     super.compareDocument(component);
 * }
 * </pre>
 * 
 * @author Andrew Cowie
 */
class QuackTestCase extends IOTestCase
{
    String filename;

    Chapter chapter;

    /**
     * Load the given file for later comparison.
     */
    protected Component loadDocument(String filename) throws ValidityException, ParsingException,
            IOException, ImproperFilenameException {
        final Manuscript manuscript;
        final File source;
        final Chapter chapter;
        final Component component;
        final String directory, basename;

        source = new File(filename);
        directory = source.getParent();
        basename = source.getName();

        /*
         * The Manuscript filename is not actually used, but sets the
         * directory the Chapter is found in. Gives us future expandability;
         * sooner or later this code will end up handling full documents no
         * doubt.
         */

        manuscript = new Manuscript();
        manuscript.setFilename(directory + "/" + this.getClass().getName() + ".parchment");
        chapter = new Chapter(manuscript);
        chapter.setFilename(basename);

        component = chapter.loadDocument();

        this.filename = filename;
        this.chapter = chapter;

        return component;
    }

    /**
     * Write the given Component out to disk and compare it to the document
     * that this QuackTestCase was loaded from in
     * {@link #loadDocument(String)}
     */
    protected void compareDocument(Component component) throws IOException {
        final ByteArrayOutputStream out;
        final String original, result;

        original = loadFileIntoString(filename);

        out = new ByteArrayOutputStream();
        chapter.saveDocument(component, out);

        result = out.toString();
        assertEquals(original, result);
    }
}
