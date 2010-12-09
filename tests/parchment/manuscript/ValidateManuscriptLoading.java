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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import quill.client.IOTestCase;

/**
 * Test loading <code>&lt;manuscript&gt;</code> documents into Manuscript
 * objects.
 * 
 * @author Andrew Cowie
 */
public class ValidateManuscriptLoading extends IOTestCase
{

    /*
     * Have XOM load our reference .parchment file, and see what it gets us.
     */
    private static Document load(String filename) throws Exception {
        final File source;
        final Builder parser;
        final Document doc;

        source = new File(filename);

        parser = new Builder();
        doc = parser.build(source);

        return doc;
    }

    /*
     * Manually use XOM API to test our reference .parchment file against
     * hardcoded values here. This is rigid in the extreme! What would be a
     * better test?
     */
    public final void testManualLoadFile() throws Exception {
        final Document parsed;
        Elements elements;
        final Element manuscript, content, presentation, metadata;
        final Element renderer, paper, margins, document, author, spelling;
        Element chapter, font;
        Attribute attribute;
        String name, value;
        String[] expected, sides, widths, faces, desc, sizes;
        int i, num;

        parsed = load("xml/Example.parchment");

        /*
         * Check <manuscript> root element
         */

        manuscript = parsed.getRootElement();
        name = manuscript.getLocalName();
        assertEquals("manuscript", name);

        elements = manuscript.getChildElements();
        num = elements.size();
        assertEquals(3, num);

        /*
         * Check <content> and <presentation> toplevels
         */

        content = elements.get(0);
        name = content.getLocalName();
        assertEquals("content", name);

        presentation = elements.get(1);
        name = presentation.getLocalName();
        assertEquals("presentation", name);

        metadata = elements.get(2);
        name = metadata.getLocalName();
        assertEquals("metadata", name);

        /*
         * Descend into <content>
         */

        elements = content.getChildElements();
        num = elements.size();
        assertEquals(2, num);

        expected = new String[] {
            "Introduction.xml",
            "Final Everywhere.xml"
        };

        elements = content.getChildElements();
        for (i = 0; i < 2; i++) {
            chapter = elements.get(i);
            name = chapter.getLocalName();
            assertEquals("chapter", name);

            num = chapter.getAttributeCount();
            assertEquals(1, num);
            attribute = chapter.getAttribute(0);
            name = attribute.getLocalName();
            assertEquals("src", name);
            value = attribute.getValue();
            assertEquals(expected[i], value);
        }

        /*
         * Descend into <presentation>
         */

        elements = presentation.getChildElements();
        num = elements.size();
        assertEquals(7, num);

        /*
         * Check <renderer> element
         */

        renderer = elements.get(0);
        name = renderer.getLocalName();
        assertEquals("renderer", name);

        num = renderer.getAttributeCount();
        assertEquals(1, num);
        attribute = renderer.getAttribute(0);
        name = attribute.getLocalName();
        assertEquals("class", name);
        value = attribute.getValue();
        assertEquals("parchment.render.ReportRenderEngine", value);

        /*
         * Check <paper> element
         */

        paper = elements.get(1);
        name = paper.getLocalName();
        assertEquals("paper", name);

        num = paper.getAttributeCount();
        assertEquals(1, num);
        attribute = paper.getAttribute(0);
        name = attribute.getLocalName();
        assertEquals("size", name);
        value = attribute.getValue();
        assertEquals("A4", value);

        /*
         * Check <margin> element
         */

        sides = new String[] {
            "top",
            "left",
            "right",
            "bottom"
        };

        widths = new String[] {
            "15.0",
            "20.0",
            "12.5",
            "10.0"
        };

        margins = elements.get(2);
        name = margins.getLocalName();
        assertEquals("margins", name);

        num = margins.getAttributeCount();
        assertEquals(4, num);
        for (i = 0; i < 4; i++) {
            attribute = margins.getAttribute(i);
            name = attribute.getLocalName();
            assertEquals(sides[i], name);
            value = attribute.getValue();
            assertEquals(widths[i], value);
        }

        /*
         * Check <font> elements
         */

        faces = new String[] {
            "serif",
            "sans",
            "mono",
            "heading"
        };

        desc = new String[] {
            "Linux Libertine O",
            "Liberation Sans",
            "Inconsolata",
            "Linux Libertine O C"
        };
        sizes = new String[] {
            "3.2",
            "2.6",
            "3.0",
            "5.6"
        };

        for (i = 0; i < 4; i++) {
            font = elements.get(i + 3);
            name = font.getLocalName();
            assertEquals("font", name);

            num = font.getAttributeCount();
            assertEquals(2, num);

            attribute = font.getAttribute(0);
            name = attribute.getLocalName();
            assertEquals(faces[i], name);
            value = attribute.getValue();
            assertEquals(desc[i], value);

            attribute = font.getAttribute(1);
            name = attribute.getLocalName();
            assertEquals("size", name);
            value = attribute.getValue();
            assertEquals(sizes[i], value);
        }

        /*
         * Descend into <metadata>
         */

        elements = metadata.getChildElements();
        num = elements.size();
        assertEquals(3, num);

        /*
         * Check <document> element
         */

        document = elements.get(0);
        name = document.getLocalName();
        assertEquals("document", name);

        num = document.getAttributeCount();
        assertEquals(1, num);
        attribute = document.getAttribute(0);
        name = attribute.getLocalName();
        assertEquals("title", name);
        value = attribute.getValue();
        assertEquals("Reference Document", value);

        /*
         * Check <author> element
         */

        author = elements.get(1);
        name = author.getLocalName();
        assertEquals("author", name);

        num = author.getAttributeCount();
        assertEquals(1, num);
        attribute = author.getAttribute(0);
        name = attribute.getLocalName();
        assertEquals("name", name);
        value = attribute.getValue();
        assertEquals("George Jones", value);

        /*
         * Check <spelling> element
         */

        spelling = elements.get(2);
        name = spelling.getLocalName();
        assertEquals("spelling", name);

        num = author.getAttributeCount();
        assertEquals(1, num);
        attribute = spelling.getAttribute(0);
        name = attribute.getLocalName();
        assertEquals("lang", name);
        value = attribute.getValue();
        assertEquals("en_CA", value);
    }
}
