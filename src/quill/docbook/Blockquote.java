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

/**
 * A verse or quote as a passage.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO whitespace?
 */
public class Blockquote extends BlockElement implements Block
{
    public Blockquote() {
        super("blockquote");
    }

    /**
     * The text body of a blockquote is a series of para elements.
     */
    public void add(Paragraph para) {
        super.add(para);
    }
}
