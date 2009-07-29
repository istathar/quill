/*
 * InlineElement.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * Common implementation of Inline
 * 
 * @author Andrew Cowie
 */
abstract class InlineElement extends DocBookElement
{
    InlineElement(String name) {
        super(name);
    }

    public void add(String text) {
        super.add(text);
    }
}
