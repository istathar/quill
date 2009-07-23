/*
 * PreformatSegment.java
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
import quill.docbook.ProgramListing;

/**
 * A section of text edited as a constant width block. These are presented in
 * the UI as a text editor with constant width fonts. It will correspond 1:1
 * to a ProgramListing Block when written out as DocBook, with newlines
 * preserved rather than used to splice in to multiple Paragraphs.
 * 
 * @author Andrew Cowie
 */
public final class PreformatSegment extends Segment
{
    public PreformatSegment() {
        super();
    }

    public Block createBlock() {
        return new ProgramListing();
    }

    Segment createSimilar() {
        final Segment result;

        result = new PreformatSegment();
        result.setParent(this.getParent());

        return result;
    }
}
