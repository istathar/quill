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

    private ManuscriptElement metadata;

    public ManuscriptConverter(final Folio folio) {
        initialStructure();
        buildContent(folio);
        buildPresentation(folio);
        buildMetadata(folio);
    }

    private void initialStructure() {
        root = new ManuscriptElement("manuscript");

        content = new ManuscriptElement("content");
        root.appendChild(content);

        presentation = new ManuscriptElement("presentation");
        root.appendChild(presentation);

        metadata = new ManuscriptElement("metadata");
        root.appendChild(metadata);
    }

    private void buildContent(final Folio folio) {
        int i;
        Chapter chapter;
        ManuscriptElement element;
        Attribute attribute;
        String filename;

        for (i = 0; i < folio.size(); i++) {
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
    private void buildPresentation(final Folio folio) {
        final Stylesheet style;
        ManuscriptElement renderer, paper, margins, font;
        Attribute attribute;
        String value;

        style = folio.getStylesheet();

        renderer = new ManuscriptElement("renderer");
        value = style.getRendererClass();
        attribute = new Attribute("class", value);
        renderer.addAttribute(attribute);
        presentation.appendChild(renderer);

        paper = new ManuscriptElement("paper");
        value = style.getPaperSize();
        attribute = new Attribute("size", value);
        paper.addAttribute(attribute);
        presentation.appendChild(paper);

        margins = new ManuscriptElement("margins");
        value = style.getMarginTop();
        attribute = new Attribute("top", value);
        margins.addAttribute(attribute);
        value = style.getMarginLeft();
        attribute = new Attribute("left", value);
        margins.addAttribute(attribute);
        value = style.getMarginRight();
        attribute = new Attribute("right", value);
        margins.addAttribute(attribute);
        value = style.getMarginBottom();
        attribute = new Attribute("bottom", value);
        margins.addAttribute(attribute);
        presentation.appendChild(margins);

        font = new ManuscriptElement("font");
        value = style.getFontSerif();
        attribute = new Attribute("serif", value);
        font.addAttribute(attribute);
        value = style.getSizeSerif();
        attribute = new Attribute("size", value);
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        value = style.getFontSans();
        attribute = new Attribute("sans", value);
        font.addAttribute(attribute);
        value = style.getSizeSans();
        attribute = new Attribute("size", value);
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        value = style.getFontMono();
        attribute = new Attribute("mono", value);
        font.addAttribute(attribute);
        value = style.getSizeMono();
        attribute = new Attribute("size", value);
        font.addAttribute(attribute);
        presentation.appendChild(font);

        font = new ManuscriptElement("font");
        value = style.getFontHeading();
        attribute = new Attribute("heading", value);
        font.addAttribute(attribute);
        value = style.getSizeHeading();
        attribute = new Attribute("size", value);
        font.addAttribute(attribute);
        presentation.appendChild(font);
    }

    private void buildMetadata(final Folio folio) {
        final Metadata meta;
        ManuscriptElement document, author;
        Attribute attribute;
        String value;

        meta = folio.getMetadata();

        document = new ManuscriptElement("document");
        value = meta.getDocumentTitle();
        attribute = new Attribute("title", value);
        document.addAttribute(attribute);
        value = meta.getDocumentLang();
        attribute = new Attribute("lang", value);
        document.addAttribute(attribute);
        metadata.appendChild(document);

        author = new ManuscriptElement("author");
        value = meta.getAuthorName();
        attribute = new Attribute("name", value);
        author.addAttribute(attribute);
        metadata.appendChild(author);
    }

    /**
     * Create a <code>&lt;manuscript&gt;</code> object based on what has been
     * fed to the converter, and write it to the given stream.
     */
    public void writeManuscript(OutputStream out) throws IOException {
        root.toXML(out);
    }
}
