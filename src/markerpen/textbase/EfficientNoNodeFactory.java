/*
 * EfficientNoNodeFactory.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

import java.util.ArrayList;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

/**
 * Use XOM as a front end to whatever XML parser and then work its callbacks
 * to extract text & format information. Fun name. Points out that we're not
 * actually building a NodeTree with this, but in fact building up a Chunk[]
 * that ultimately can be used to construct a Text.
 * 
 * @author Andrew Cowie
 */
public class EfficientNoNodeFactory extends NodeFactory
{
    private static final Nodes empty;

    static {
        empty = new Nodes();
    }

    private final ArrayList<Span> list;

    /**
     * The (single) formatting applicable at this insertion point.
     */
    private Markup markup;

    /**
     * Did we just enter a block level element?
     */
    private boolean block;

    /**
     * Convert line endings to LINE SEPARATOR?
     */
    private boolean convert;

    public EfficientNoNodeFactory() {
        list = new ArrayList<Span>(32);
    }

    public TextStack createText() {
        final TextStack result;

        result = new TextStack();

        for (Span span : list) {
            result.append(span);
        }

        return result;
    }

    /*
     * The rest of this class are overrides of the NodeFactory parent class.
     */

    public Nodes makeText(String text) {
        final String trim, str;
        final int len;
        boolean first, last;

        len = text.length();
        first = false;
        last = false;

        if (len == 0) {
            return empty;
        }

        if (len == 1) {
            if (text.charAt(0) == '\n') {
                return empty;
            }
        }

        if (block) {
            block = false;
        } else {
            list.add(new CharacterSpan(' ', markup));
        }

        trim = text.trim();

        if (convert) {
            str = trim.replace('\n', '\u2028');
        } else {
            str = trim.replace('\n', ' ');
        }

        list.add(new StringSpan(str, markup));
        return empty;
    }

    /*
     * The only state we carry around is what markup this element represents,
     * and whether to convert line endings. So we set those two things, and
     * then return.
     */
    public Element startMakingElement(String name, String namespace) {
        /*
         * Block elements are so common that we handle them first and bail out
         * of here.
         */

        if (name.equals("para")) {
            markup = null;
            block = true;
            convert = false;
            return null;
        } else if (name.equals("programlisting")) {
            markup = Preformat.NORMAL;
            block = true;
            convert = true;
            return null;
        }

        /*
         * Otherwise we're dealing with an inline spanning element.
         */

        block = false;
        convert = false;

        if (name.equals("function")) {
            markup = Common.FUNCTION;
        } else if (name.equals("filename")) {
            markup = Common.FILENAME;
        } else if (name.equals("type")) {
            markup = Common.TYPE;
        } else if (name.equals("emphasis")) {
            markup = Common.ITALICS;
            // TODO what about bold?
        } else {
            /*
             * No need to warn, really. The structure tags don't count. But if
             * we're losing semantic data, this is where its happening.
             */
            markup = null;
        }

        return null;
    }

    /*
     * From here on we're deliberately faking Builder out, only returning the
     * minimum necessary for it to still function.
     */

    public Element makeRootElement(String name, String namespace) {
        return new Element("fake");
    }

    public Nodes finishMakingElement(Element element) {
        return new Nodes(element);
    }

    public void finishMakingDocument(Document document) {

    }

    public Nodes makeAttribute(String name, String uri, String value, Attribute.Type type) {
        return empty;
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
