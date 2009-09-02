/*
 * DocBookNodeFactory.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

/**
 * Use XOM as a front end to whatever XML parser and then work its callbacks
 * to create quill.docbook wrapper objects.
 * 
 * @author Andrew Cowie
 */
public class DocBookNodeFactory extends NodeFactory
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
    public Element startMakingElement(String name, String namespace) {
        if (name.equals("para")) {
            return new Paragraph();
        } else if (name.equals("programlisting")) {
            return new ProgramListing();
        } else if (name.equals("blockquote")) {
            return new Blockquote();
        } else if (name.equals("section")) {
            return new Section();
        } else if (name.equals("chapter")) {
            return new Chapter();
        } else if (name.equals("title")) {
            return new Title();
        } else if (name.equals("function")) {
            return new Function();
        } else if (name.equals("filename")) {
            return new Filename();
        } else if (name.equals("type")) {
            return new Type();
        } else if (name.equals("literal")) {
            return new Literal();
        } else if (name.equals("command")) {
            return new Command();
        } else if (name.equals("application")) {
            return new Application();
        } else if (name.equals("userinput")) {
            throw new UnsupportedOperationException("Implement UserInput class");
        } else if (name.equals("emphasis")) {
            return new Emphasis();
        } else {
            /*
             * This is actually fairly serious; once our code is working
             * properly, hitting this means that you are trying to read
             * invalid DocBook (or rather, not the Quack subset this program
             * deals with).
             */
            // FIXME this needs a far more comprehensive message
            throw new IllegalStateException("Unknown element " + name);
        }
    }

    public Nodes makeAttribute(String name, String URI, String value, Attribute.Type type) {
        /*
         * TODO validate schema inbound here?
         */
        /*
         * The one attribute we work with is emphasis's bold. Do we need to do
         * anything special there for that, or is that logic properly in
         * DocBookLoader?
         */
        return super.makeAttribute(name, URI, value, type);
    }

    /*
     * From here on we're deliberately faking Builder out, only returning the
     * minimum necessary for it to still function.
     */

    public Element makeRootElement(String name, String namespace) {
        if (name.equals("chapter")) {
            return new Chapter();
        } else if (name.equals("article")) {
            return new Article();
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
