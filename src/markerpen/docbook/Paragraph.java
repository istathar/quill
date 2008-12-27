/*
 * Paragraph.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.docbook;

public class Paragraph extends Block
{
    public Paragraph() {
        super("para");
    }

    public Paragraph(String str) {
        super("para");
        super.addText(str);
    }

    public void add(String str) {
        super.addText(str);
    }

    public void add(Inline tag) {
        super.addChild(tag);
    }
}
