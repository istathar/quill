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
package parchment.format;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Serializer;

/**
 * Internal root class for &lt;manuscript&gt; tags.
 * 
 * @author Andrew Cowie
 */
class ManuscriptElement extends Element
{
    ManuscriptElement(String name) {
        super(name, "http://namespace.operationaldynamics.com/parchment/0.2");
    }

    void setValue(String name, String value) {
        super.addAttribute(new Attribute(name, value));
    }

    /**
     * @deprecated
     */
    String getValue(String name) {
        final Attribute a;

        a = super.getAttribute(name);

        return a.getValue();
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
    // move this to ManuscriptConverter?
    public void toXML(OutputStream out) throws IOException {
        final Document doc;
        final Serializer s;

        doc = new Document(this);

        s = new ManuscriptSerializer(out, this);
        s.write(doc);
        s.flush();
    }

    public String toString() {
        return "<" + getQualifiedName() + ">";
    }

    /**
     * @deprecated
     */
    ManuscriptElement[] getChildren() {
        final ManuscriptElement[] result;
        final int num;
        final Elements children;
        int i;

        num = super.getChildCount();
        result = new ManuscriptElement[num];

        children = getChildElements();

        for (i = 0; i < num; i++) {
            result[i] = (ManuscriptElement) children.get(i);
        }

        return result;
    }
}
