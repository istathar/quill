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

public class ValidateFileNaming extends IOTestCase
{
    public final void testInsertText() throws ValidityException, ParsingException, IOException {
        final Manuscript manuscript;
        final Folio folio;
        final File target;
        final String parentdir, basename, filename;

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        try {
            manuscript.saveDocument(folio);
            fail("Lack of name not trapped");
        } catch (IllegalStateException ise) {
            // good
        }

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

        manuscript.saveDocument(folio);

        assertTrue(target.exists());
    }
}
