/*
 * Inline.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * Tags that are presented as spans within Blocks.
 * 
 * @author Andrew Cowie
 */
public abstract class Inline extends DocBookTag
{
    Inline(String name) {
        super(name);
    }

    public void add(String str) {
        super.addText(str);
    }
}
