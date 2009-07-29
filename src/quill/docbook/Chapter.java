/*
 * Chapter.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * A chapter in a book. See also Article.
 * 
 * @author Andrew Cowie
 */
public class Chapter extends StructureElement implements Component
{
    public Chapter() {
        super("chapter");
    }

    public void add(Division section) {
        super.add(section);
    }
}
