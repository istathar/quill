/*
 * DocBookTag.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Internal root class for all DocBook tags. This is a wrapper around the
 * delelgation to the XOM Element class that is used internally to build the
 * XML when it's time to write to disk.
 * 
 * @author Andrew Cowie
 */
abstract class DocBookTag
{
    final Element element;

    DocBookTag(String name) {
        this.element = new DocBookElement(name, this);
    }

    void addChild(DocBookTag tag) {
        element.appendChild(tag.element);
    }

    void addText(String text) {
        element.appendChild(text);
    }

    void setAttribute(String name, String value) {
        element.addAttribute(new Attribute(name, value));
    }

    String getAttribute(String name) {
        final Attribute current;

        current = element.getAttribute(name);
        if (current != null) {
            return current.getValue();
        } else {
            return null;
        }
    }

    /**
     * Set the <code>xml:space="preserve"</code> Attribute on an Element.
     */
    /*
     * How nasty is this expression! No kidding we wrap it up.
     */
    void setPreserveWhitespace() {
        element.addAttribute(new Attribute("xml:space", "http://www.w3.org/XML/1998/namespace",
                "preserve"));
    }

    /**
     * Serialize this document to XML.
     */
    // move this to DocBookConverter?
    public void toXML(OutputStream out) throws IOException {
        final Document doc;
        final Serializer s;

        doc = new Document(this.element);

        /*
         * The top level element in a DocBook XML document has to have a
         * version attribute.
         */
        this.setAttribute("version", "5.0");

        s = new DocBookSerializer(out, this);
        s.write(doc);
        s.flush();
    }
}

/**
 * Quick wrapper class allowing us to get from the Element back to the deep
 * Tag hierarcy class which represents it.
 */
final class DocBookElement extends Element
{
    final DocBookTag proxy;

    DocBookElement(String name, DocBookTag enclosing) {
        super(name, "http://docbook.org/ns/docbook");
        proxy = enclosing;
    }
}
