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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * Simplistic methods to reach into a <code>&lt;manuscript&gt;</code> XOM tree
 * and get values out of it. This assumes that the document is valid!
 * 
 * @author Andrew Cowie
 */
/*
 * Just use the XOM API directly.
 */
class ManuscriptLoader
{
    private String[] chapterSources;

    ManuscriptLoader(Document document) throws InvalidDocumentException {
        final Element root;

        root = document.getRootElement();

        processManuscript(root);
    }

    private void processManuscript(Element root) throws InvalidDocumentException {
        final Elements children;
        final int num;
        String name;
        final Element content;
        final Element presentation;

        children = root.getChildElements();
        num = children.size();

        if (num != 2) {
            throw new InvalidDocumentException("Should be exactly 2 elements in <manuscript>, not "
                    + num);
        }

        content = children.get(0);
        name = content.getLocalName();
        if (!name.equals("content")) {
            throw new InvalidDocumentException(
                    "The first element of <manuscript> should be <content>, not <" + name + ">");
        }

        presentation = children.get(1);
        name = presentation.getLocalName();
        if (!name.equals("presentation")) {
            throw new InvalidDocumentException(
                    "The second element of <manuscript> should be <presentation>, not <" + name + ">");
        }

        processContent(content);
        processPresentation(presentation);
    }

    private void processContent(Element content) throws InvalidDocumentException {
        final Elements children;
        final int num;
        int i;
        Element chapter;
        String src, name;

        children = content.getChildElements();
        num = children.size();
        chapterSources = new String[num];

        for (i = 0; i < num; i++) {
            chapter = children.get(i);

            name = content.getLocalName();
            if (!name.equals("content")) {
                throw new InvalidDocumentException(
                        "The elements of <content> are <chapter>; encountered a <" + name
                                + "> which is invalid");
            }

            src = getValue(chapter, "src");
            chapterSources[i] = src;
        }
    }

    private static String getValue(Element element, String name) throws InvalidDocumentException {
        final Attribute a;

        a = element.getAttribute(name);

        if (a == null) {
            throw new InvalidDocumentException("Looked for attribute \"" + name + "\" in <"
                    + element.getLocalName() + "> but not found");
        }

        return a.getValue();
    }

    private void processPresentation(Element presentation) {
    // TODO Auto-generated method stub

    }

    String[] getChapterSources() {
        return chapterSources;
    }

}
