/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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
package quill.quack;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

/**
 * Use XOM as a front end to whatever XML parser and then work its callbacks
 * to create quill.quack wrapper objects.
 * 
 * @author Andrew Cowie
 */
public class QuackNodeFactory extends NodeFactory
{
    private static final Nodes empty;

    static {
        empty = new Nodes();
    }

    public Nodes makeText(String text) {
        return super.makeText(text);
    }

    /*
     * The only state we carry around is what markup this element represents,
     * and whether to convert line endings. So we set those two things, and
     * then return.
     */
    /*
     * We intern the element name for two reasons. A tinsy speed improvement
     * over equals(), of course, but also because we do end up making a fair
     * number of of these, and better to use the same object.
     */
    public Element startMakingElement(String local, String namespace) {
        final String name;

        name = local.intern();

        if (name == "text") {
            return new TextElement();
        } else if (name == "code") {
            return new CodeElement();
        } else if (name == "quote") {
            return new QuoteElement();
        } else if (name == "poem") {
            return new PoemElement();
        } else if (name == "credit") {
            return new CreditElement();
        } else if (name == "title") {
            return new TitleElement();
        } else if (name == "heading") {
            return new HeadingElement();
        } else if (name == "image") {
            return new ImageElement();
        } else if (name == "function") {
            return new FunctionElement();
        } else if (name == "filename") {
            return new FilenameElement();
        } else if (name == "type") {
            return new TypeElement();
        } else if (name == "literal") {
            return new LiteralElement();
        } else if (name == "command") {
            return new CommandElement();
        } else if (name == "highlight") {
            return new HighlightElement();
        } else if (name == "publication") {
            return new PublicationElement();
        } else if (name == "keyboard") {
            return new KeyboardElement();
        } else if (name == "acronym") {
            return new AcronymElement();
        } else if (name == "project") {
            return new ProjectElement();
        } else if (name == "userinput") {
            throw new UnsupportedOperationException("Implement a UserInputElement class");
        } else if (name == "italics") {
            return new ItalicsElement();
        } else if (name == "bold") {
            return new BoldElement();
        } else if (name == "note") {
            return new NoteElement();
        } else if (name == "cite") {
            return new CiteElement();
        } else {
            /*
             * This is actually fairly serious; once our code is working
             * properly, hitting this means that you are trying to read
             * invalid Quack.
             */
            // FIXME this needs a far more comprehensive message
            throw new IllegalStateException("Unknown element " + name);
        }
    }

    public Nodes makeAttribute(String name, String URI, String value, Attribute.Type type) {
        /*
         * Eeek. This means any src attribute. No contextual awareness.
         */
        if (name.equals("src")) {
            return new Nodes(new SourceAttribute(value));
        } else {
            return super.makeAttribute(name, URI, value, type);
        }
    }

    /*
     * From here on we're deliberately faking Builder out, only returning the
     * minimum necessary for it to still function.
     */

    public Element makeRootElement(String name, String namespace) {
        if (name.equals("chapter")) {
            return new ChapterElement();
        } else if (name.equals("article")) {
            throw new UnsupportedOperationException("Implement ArticleElement?");
        } else {
            throw new IllegalStateException("Invalid document");
        }
    }

    public Nodes makeComment(String text) {
        return empty;
    }

    public Nodes makeProcessingInstruction(String target, String data) {
        return empty;
    }

    public Nodes makeDocType(String rootElementName, String publicID, String systemID) {
        return empty;
    }
}
