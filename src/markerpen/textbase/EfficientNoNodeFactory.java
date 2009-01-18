/*
 * EfficientNoNodeFactory.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
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

    private final ArrayList<Chunk> list;

    public EfficientNoNodeFactory() {
        list = new ArrayList<Chunk>(32);
    }

    public TextStack createText() {
        final TextStack result;

        result = new TextStack();

        for (Chunk chunk : list) {
            result.append(chunk);
        }

        return result;
    }

    /*
     * The rest of this class are overrides of the NodeFactory parent class.
     */

    public Nodes makeText(String text) {
        list.add(new Chunk(text.trim()));

        return empty;
    }

    public Element startMakingElement(String name, String namespace) {
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