/*
 * DocBookTag.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

import nu.xom.Attribute;
import nu.xom.Element;

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
