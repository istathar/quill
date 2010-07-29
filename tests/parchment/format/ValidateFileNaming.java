/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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
package parchment.format;

import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.Folio;
import quill.textbase.Series;

public class ValidateFileNaming extends IOTestCase
{
    public final void testManuscriptName() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio;
        final File target;
        final String parentdir, basename, filename;

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        assertNotNull(folio);
        assertEquals("Untitled", manuscript.getBasename());

        target = new File("tmp/unittests/parchment/format/ValidateFileNaming.parchment");
        target.delete();
        assertFalse(target.exists());
        target.getParentFile().mkdirs();

        try {
            manuscript.setFilename(target.getPath());
        } catch (ImproperFilenameException e) {
            fail("Shouldn't have thrown");
        }

        parentdir = manuscript.getDirectory();
        basename = manuscript.getBasename();
        filename = manuscript.getFilename();

        assertTrue(parentdir.endsWith("tmp/unittests/parchment/format"));
        assertEquals("ValidateFileNaming", basename);
        assertEquals(parentdir + "/" + basename + ".parchment", filename);
    }

    public final void testChapterName() throws IOException {
        final Chapter chapter;
        final Series series;
        final File target;

        chapter = new Chapter();
        series = chapter.createDocument();

        try {
            chapter.saveDocument(series);
            fail("Lack of name not trapped");
        } catch (IllegalStateException ise) {
            // good
        }

        try {
            chapter.setFilename("something.other");
            fail("Should have rejected improper filename");
        } catch (ImproperFilenameException e) {
            // good
        }

        target = new File("tmp/unittests/parchment/format/chapter01.xml");
        target.delete();
        assertFalse(target.exists());
        target.getParentFile().mkdirs();

        try {
            chapter.setFilename(target.getPath());
        } catch (ImproperFilenameException e) {
            fail("Shouldn't have thrown");
        }

    }

    public final void testChapterRelative() throws IOException {
        final Manuscript manuscript;
        final Folio folio;
        final Chapter chapter, another;
        final String relative, second;

        chapter = new Chapter();

        try {
            chapter.setFilename("relative.xml");

        } catch (ImproperFilenameException ife) {
            fail(ife.getMessage());
        }

        try {
            chapter.getRelative();
            fail("No parent Manuscript, so getRelative() should have been guarded");
        } catch (IllegalStateException ise) {
            // good
        }

        /*
         * Ok, so how about
         */

        manuscript = new Manuscript();
        try {
            manuscript.setFilename("tmp/unittests/parchment/format/ValidateFileNaming.parchment");
        } catch (ImproperFilenameException ife) {
            fail(ife.getMessage());
        }

        folio = manuscript.createDocument();
        another = folio.getChapter(0);
        relative = another.getRelative();
        assertEquals("Chapter1.xml", relative);

        try {
            another.setFilename("tmp/unittests/parchment/format/relative.xml");
        } catch (ImproperFilenameException ife) {
            fail("Should have been ok");
        }
        second = another.getRelative();
        assertEquals("tmp/unittests/parchment/format/relative.xml", second);
    }

    // TODO
    public final void testThroughChapter() throws ValidityException, ParsingException, IOException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Folio folio;
        final File target;

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        target = new File("tmp/unittests/parchment/format/ValidateFileNaming.parchment");
        target.delete();
        assertFalse(target.exists());

        manuscript.setFilename(target.getPath());
        manuscript.saveDocument(folio);

        assertTrue("Save didn't write anything!", target.exists());

        // TODO
    }

}
