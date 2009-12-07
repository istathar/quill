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
package quill.client;

import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.textbase.DataLayer;

public class ValidateFileNaming extends IOTestCase
{
    public final void testInsertText() throws ValidityException, ParsingException, IOException {
        final DataLayer data;
        final File target;
        final String parentdir, basename, filename;

        data = new DataLayer();

        data.createDocument();

        try {
            data.saveDocument();
            fail("Lack of name not trapped");
        } catch (IllegalStateException ise) {
            // good
        }

        target = new File("tmp/unittests/quill/ui/ValidateFileNaming.xml");
        target.delete();
        assertFalse(target.exists());
        target.getParentFile().mkdirs();

        data.setFilename(target.getPath());

        parentdir = data.getDirectory();
        basename = data.getBasename();
        filename = data.getFilename();

        assertTrue(parentdir.endsWith("tmp/unittests/quill/ui"));
        assertEquals("ValidateFileNaming", basename);
        assertEquals(parentdir + "/" + basename + ".xml", filename);

        data.saveDocument();

        assertTrue(target.exists());
    }
}
