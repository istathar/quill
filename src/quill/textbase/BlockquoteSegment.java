/*
 * BlockquoteSegment.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import quill.docbook.Block;
import quill.docbook.Blockquote;

/**
 * A quote. A BlockquoteSegment may (and almost certainly will) contain more
 * than one Paragraph block since EditorWindow can handle multiple paras with
 * '\n' separators; no need for a different Widget per para.
 * 
 * @author Andrew Cowie
 */
public final class BlockquoteSegment extends Segment
{
    public BlockquoteSegment() {
        super();
    }

    public Block createBlock() {
        return new Blockquote();
    }

    Segment createSimilar() {
        final Segment result;

        result = new BlockquoteSegment();
        result.setParent(this.getParent());

        return result;
    }
}
