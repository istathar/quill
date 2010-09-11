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
package parchment.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import quill.client.IOTestCase;

/**
 * Evaluate XOM's Serializer to output our <code>&lt;manuscript&gt;</code>
 * documents.
 * 
 * @author Andrew Cowie
 */
public class ValidateManuscriptSerializing extends IOTestCase
{
    /*
     * Just test the manual code we wrote in the prototype against the
     * reference example in xml/.
     */
    public final void testManualSerializeOut() throws IOException {
        String filename, reference, exercise;
        FileOutputStream out;
        File dir;

        dir = new File("tmp/unittests/parchment/format/");

        if (!dir.exists() && (!dir.isDirectory())) {
            dir.mkdirs();
        }
        filename = "tmp/unittests/parchment/format/ValidateManuscriptSerializing.xml";

        out = new FileOutputStream(filename);
        serialize(out);
        out.close();

        exercise = loadFileIntoString(filename);
        reference = loadFileIntoString("xml/Example.parchment");

        assertEquals(reference, exercise);
    }

    /*
     * This will be moved into a real tree builder, obviously.
     */
    private static void serialize(OutputStream out) throws IOException {
        final Document document;
        final Element manuscript, content, presentation;
        final Element renderer, paper, margins;
        Element chapter, font;
        Attribute attribute;
        final Serializer serializer;

        manuscript = new ParchmentElement("manuscript");

        content = new ParchmentElement("content");
        presentation = new ParchmentElement("presentation");

        chapter = new ParchmentElement("chapter");
        attribute = new Attribute("src", "Introduction.xml");
        chapter.addAttribute(attribute);
        content.appendChild(chapter);

        chapter = new ParchmentElement("chapter");
        attribute = new Attribute("src", "Final Everywhere.xml");
        chapter.addAttribute(attribute);
        content.appendChild(chapter);

        manuscript.appendChild(content);

        renderer = new ParchmentElement("renderer");
        attribute = new Attribute("class", "com.operationaldynamics.parchment.ReportRenderEngine");
        renderer.addAttribute(attribute);
        presentation.appendChild(renderer);

        paper = new ParchmentElement("paper");
        attribute = new Attribute("size", "A4");
        paper.addAttribute(attribute);
        presentation.appendChild(paper);

        margins = new ParchmentElement("margins");
        attribute = new Attribute("top", "10.00");
        margins.addAttribute(attribute);
        attribute = new Attribute("left", "57.75");
        margins.addAttribute(attribute);
        attribute = new Attribute("right", "25.00");
        margins.addAttribute(attribute);
        attribute = new Attribute("bottom", "10.00");
        margins.addAttribute(attribute);
        presentation.appendChild(margins);

        font = new ParchmentElement("font");
        attribute = new Attribute("serif", "Linux Libertine, 9.0");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ParchmentElement("font");
        attribute = new Attribute("sans", "Liberation Sans, 8.0");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ParchmentElement("font");
        attribute = new Attribute("mono", "Inconsolata, 8.1");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ParchmentElement("font");
        attribute = new Attribute("heading", "Linux Libertine O C");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        manuscript.appendChild(presentation);

        document = new Document(manuscript);

        serializer = new Serializer(out);
        serializer.setIndent(2);
        serializer.setLineSeparator("\n");
        serializer.setMaxLength(0);
        serializer.write(document);
    }
}

class ParchmentElement extends Element
{
    ParchmentElement(String name) {
        super(name, "http://namespace.operationaldynamics.com/parchment/0.2");
    }
}
