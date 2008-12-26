/*
 * Section.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.docbook;

/**
 * Sections pack into Chapters.
 * 
 * @author Andrew Cowie
 */
public class Section extends Division
{
    public Section() {
        super("section");
    }

    public Section(String title) {
        super("section");
        super.addChild(new Title(title));
    }

    public void add(Paragraph para) {
        super.addChild(para);
    }
}
