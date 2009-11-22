/*
 * ValidateChangePropagation.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
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
