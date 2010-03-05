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

import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Evaluate XOM's Serializer to output our <code>&lt;manuscript&gt;</code>
 * documents.
 * 
 * @author Andrew Cowie
 */
public class PrototypeManuscriptSerializer
{
    public static void main(String[] args) throws IOException {
        final Document document;
        final Element manuscript, content, presentation;
        final Element renderer, paper, margins, fonts;
        Element chapter;
        final Serializer serializer;

        manuscript = new ParchmentElement("manuscript");

        content = new ParchmentElement("content");
        presentation = new ParchmentElement("presentation");

        chapter = new ParchmentElement("chapter");
        content.appendChild(chapter);
        chapter = new ParchmentElement("chapter");
        content.appendChild(chapter);
        manuscript.appendChild(content);

        renderer = new ParchmentElement("renderer");
        paper = new ParchmentElement("paper");
        margins = new ParchmentElement("margins");
        fonts = new ParchmentElement("fonts");
        presentation.appendChild(renderer);
        presentation.appendChild(paper);
        presentation.appendChild(margins);
        presentation.appendChild(fonts);
        manuscript.appendChild(presentation);

        document = new Document(manuscript);
        serializer = new Serializer(System.out);
        serializer.setIndent(2);
        serializer.write(document);
    }
}

class ParchmentElement extends Element
{
    ParchmentElement(String name) {
        super(name, "http://namespace.operationaldynamics.com/parchment/0.2");
    }
}
