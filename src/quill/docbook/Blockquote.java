/*
 * Blockquote.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

import nu.xom.Element;
import nu.xom.Elements;

/**
 * A verse or quote as a passage.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO whitespace?
 */
public class Blockquote extends BlockElement implements Block, Structure
{
    public Blockquote() {
        super("blockquote");
    }

    /**
     * The text body of a blockquote is a series of para elements.
     */
    public void add(Block block) {
        if (!(block instanceof Paragraph)) {
            throw new IllegalArgumentException("\n" + "You can only add Paragraphs to Blockquotes");
        }
        super.add(block);
    }

    /**
     * This ony returns paras.
     */
    public Block[] getBlocks() {
        final Elements children;
        Element element;
        final Block[] paras;
        final int num;
        int i;

        children = super.getChildElements();
        num = children.size();

        paras = new Block[num];
        for (i = 0; i < num; i++) {
            element = children.get(i);

            if (!(element instanceof Paragraph)) {
                throw new IllegalStateException("\n"
                        + "How did you get something other than a Paragraph in a Blockquote?");
            }
            paras[i] = (Block) element;
        }

        return paras;
    }
}
