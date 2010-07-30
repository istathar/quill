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

import nu.xom.Document;

/**
 * Simplistic methods to reach into a <code>&lt;manuscript&gt;</code> XOM tree
 * and get values out of it. This assumes that the document is valid!
 * 
 * @author Andrew Cowie
 */
/*
 * FUTURE do the exception messages in this class need escaping? Perhaps
 * that's up to the UI.
 */
class ManuscriptLoader
{
    private String[] chapterSources;

    ManuscriptLoader(Document document) throws InvalidDocumentException {
        final ManuscriptElement root;

        root = (ManuscriptElement) document.getRootElement();

        processManuscript(root);
    }

    private void processManuscript(ManuscriptElement root) throws InvalidDocumentException {
        final ManuscriptElement[] children;
        final int num;
        String name;
        final ManuscriptElement content;
        final ManuscriptElement presentation;

        children = root.getChildren();
        num = children.length;

        if (num != 2) {
            throw new InvalidDocumentException("Should be exactly 2 elements in <manuscript>, not "
                    + num);
        }

        content = children[0];
        name = content.getLocalName();
        if (!name.equals("content")) {
            throw new InvalidDocumentException(
                    "The first element of <manuscript> should be <content>, not <" + name + ">");
        }

        presentation = children[1];
        name = content.getLocalName();
        if (!name.equals("presentation")) {
            throw new InvalidDocumentException(
                    "The second element of <manuscript> should be <presentation>, not <" + name + ">");
        }

        processContent(content);
        processPresentation(presentation);

    }

    private void processContent(ManuscriptElement content) throws InvalidDocumentException {
        final ManuscriptElement[] children;
        final int num;
        int i;
        ManuscriptElement chapter;
        String src, name;

        children = content.getChildren();
        num = children.length;
        chapterSources = new String[num];

        for (i = 0; i < num; i++) {
            chapter = children[i];

            name = content.getLocalName();
            if (!name.equals("content")) {
                throw new InvalidDocumentException(
                        "The elements of <content> are <chapter>; encountered a <" + name
                                + "> which is invalid");
            }

            src = chapter.getValue("src");
            chapterSources[i] = src;
        }
    }

    private void processPresentation(ManuscriptElement presentation) {
    // TODO Auto-generated method stub

    }

}
