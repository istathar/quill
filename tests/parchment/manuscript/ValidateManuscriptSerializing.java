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
package parchment.manuscript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.Folio;
import quill.textbase.Series;

/**
 * Evaluate XOM's Serializer to output our <code>&lt;manuscript&gt;</code>
 * documents.
 * 
 * @author Andrew Cowie
 */
public class ValidateManuscriptSerializing extends IOTestCase
{
    /*
     * This is currently a round trip test. That's fine, but I'd rather create
     * something
     */
    public final void testSerializeOut() throws IOException, ImproperFilenameException,
            ValidityException, InvalidDocumentException, ParsingException {
        String filename, reference, exercise;
        FileOutputStream out;
        File dir;
        Manuscript manuscript;
        Chapter chapter;
        Series series;
        Folio folio;
        Stylesheet style;
        Metadata meta;
        ManuscriptConverter converter;

        dir = new File("tmp/unittests/parchment/manuscript/");

        if (!dir.exists() && (!dir.isDirectory())) {
            dir.mkdirs();
        }
        filename = "tmp/unittests/parchment/manuscript/ValidateManuscriptSerializing.parchment";
        out = new FileOutputStream(filename);

        manuscript = new Manuscript();
        folio = manuscript.createDocument();

        chapter = folio.getChapter(0);
        series = folio.getSeries(0);
        style = new Stylesheet("parchment.render.ReportRenderEngine", "A4", "15.0", "20.0", "12.5",
                "10.0", "Linux Libertine O", "Liberation Sans", "Inconsolata", "Linux Libertine O C",
                "3.2", "2.6", "3.0", "5.6");
        meta = new Metadata("Untitled", "", "en_CA");

        folio = new Folio(manuscript, chapter, series, style, meta);

        converter = new ManuscriptConverter(folio);
        converter.writeManuscript(out);

        out.close();

        exercise = loadFileIntoString(filename);
        reference = loadFileIntoString("tests/parchment/manuscript/ValidateManuscriptSerializing.parchment");

        assertEquals(reference, exercise);
    }
}
