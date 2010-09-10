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
import java.io.OutputStream;

import nu.xom.Attribute;
import quill.textbase.Folio;
import quill.textbase.Series;

/**
 * Utility to create a XOM document for a Manuscript and drive serializing it
 * out.
 * 
 * @author Andrew Cowie
 */
/*
 * Take a different approach here; just use the XOM library API as is.
 */
class ManuscriptConverter
{
    private ManuscriptElement root;

    private ManuscriptElement content;

    private ManuscriptElement presentation;

    public ManuscriptConverter(final Folio folio) {
        initialStructure();
        buildContent(folio);
        buildPresentation();
    }

    private void initialStructure() {
        root = new ManuscriptElement("manuscript");

        content = new ManuscriptElement("content");
        root.appendChild(content);

        presentation = new ManuscriptElement("presentation");
        root.appendChild(presentation);
    }

    private void buildContent(final Folio folio) {
        int i;
        Series series;
        Chapter chapter;
        ManuscriptElement element;
        Attribute attribute;
        String filename;

        for (i = 0; i < folio.size(); i++) {
            series = folio.getSeries(i);
            chapter = folio.getChapter(i);
            filename = chapter.getRelative();

            element = new ManuscriptElement("chapter");
            attribute = new Attribute("src", filename);
            element.addAttribute(attribute);
            content.appendChild(element);
        }
    }

    /**
     * A goodly chunk of the point of this is to enforce a consistent element
     * order. Nothing worse than spurious diffs just because the runtime
     * changed its sorting on output.
     */
    // HARDCODE
    private void buildPresentation() {
        ManuscriptElement renderer, paper, margins, font;
        Attribute attribute;

        renderer = new ManuscriptElement("renderer");
        attribute = new Attribute("class", "com.operationaldynamics.parchment.ReportRenderEngine");
        renderer.addAttribute(attribute);
        presentation.appendChild(renderer);

        paper = new ManuscriptElement("paper");
        attribute = new Attribute("size", "A4");
        paper.addAttribute(attribute);
        presentation.appendChild(paper);

        margins = new ManuscriptElement("margins");
        attribute = new Attribute("top", "10.00");
        margins.addAttribute(attribute);
        attribute = new Attribute("left", "57.75");
        margins.addAttribute(attribute);
        attribute = new Attribute("right", "25.00");
        margins.addAttribute(attribute);
        attribute = new Attribute("bottom", "10.00");
        margins.addAttribute(attribute);
        presentation.appendChild(margins);

        font = new ManuscriptElement("font");
        attribute = new Attribute("serif", "Linux Libertine, 9.0");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        attribute = new Attribute("sans", "Liberation Sans, 8.0");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        attribute = new Attribute("mono", "Inconsolata, 8.1");
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        attribute = new Attribute("heading", "Linux Libertine O C");
        font.addAttribute(attribute);
        presentation.appendChild(font);
    }

    /**
     * Create a <code>&lt;manuscript&gt;</code> object based on what has been
     * fed to the converter, and write it to the given stream.
     */
    public void writeManuscript(OutputStream out) throws IOException {
        root.toXML(out);
    }
}
