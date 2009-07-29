/*
 * Paragraph.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

public class Paragraph extends Block
{
    public Paragraph() {
        super("para");
    }

    public Paragraph(String str) {
        super("para");
        super.addText(str);
    }

    /**
     * Paragraphs are unusual (and annoying) in that they can contain blocks.
     */
    public void add(Block block) {
        if (block instanceof Paragraph) {
            throw new IllegalArgumentException("Can't nest <para> in <para>");
        }
        super.addChild(block);
    }
}
