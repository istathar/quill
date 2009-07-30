/*
 * DocBookElement.java
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
 * Internal root class for all DocBook tags.
 * 
 * @author Andrew Cowie
 */
abstract class DocBookElement extends Element
{
    DocBookElement(String name) {
        super(name, "http://docbook.org/ns/docbook");
    }

    void add(Tag tag) {
        super.appendChild((DocBookElement) tag);
    }

    void add(String str) {
        super.appendChild(str);
    }

    void setAttribute(String name, String value) {
        super.addAttribute(new Attribute(name, value));
    }

    /**
     * Set the <code>xml:space="preserve"</code> Attribute on an Element.
     */
    /*
     * How nasty is this expression! No kidding we wrap it up.
     */
    void setPreserveWhitespace() {
        super.addAttribute(new Attribute("xml:space", "http://www.w3.org/XML/1998/namespace", "preserve"));
    }

    /**
     * Serialize this document to XML.
     */
    // move this to DocBookConverter?
    public void toXML(OutputStream out) throws IOException {
        final Document doc;
        final Serializer s;

        doc = new Document(this);

        /*
         * The top level element in a DocBook XML document has to have a
         * version attribute.
         */
        this.setAttribute("version", "5.0");

        s = new DocBookSerializer(out, this);
        s.write(doc);
        s.flush();
    }

    public String toString() {
        return "<" + getQualifiedName() + ">";
    }
}
