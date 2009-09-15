/*
 * ChapterElement.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

import nu.xom.Elements;

/**
 * A chapter in a book.
 * 
 * @author Andrew Cowie
 */
public class ChapterElement extends QuackElement implements Component
{
    public ChapterElement() {
        super("chapter");
    }

    /**
     * Blocks can be added to Components and Divisions.
     */
    public void add(Block block) {
        super.add(block);
    }

    /**
     * Get the children Blocks as an array.
     */
    public Block[] getBlocks() {
        final Elements children;
        int i;
        final int num;
        final Block[] result;

        children = super.getChildElements();

        num = children.size();
        result = new Block[num];

        for (i = 0; i < num; i++) {
            result[i] = (Block) children.get(i);
        }

        return result;
    }
}
