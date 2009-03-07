/*
 * Segment.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

import markerpen.docbook.Block;

/**
 * Segments are the block level grouping mechanism in textbase. Concrete
 * instances of this represent the entities we model as UI blocks each with
 * their own customized EditorWidget.
 * 
 * <p>
 * Note that some of these logical structures may correspond to more than one
 * underlying DocBook block level element; see {@link ParagraphSegment}.
 * 
 * <p>
 * Segment itself does not provide a chaining or sequencing mechanism; impose
 * that with a Segment[] or a more complicated mechanism as necessary.
 * 
 * @author Andrew Cowie
 */
public abstract class Segment
{
    /**
     * The internal representation of the text body of this Segment.
     */
    private TextStack stack;

    public TextStack getText() {
        return stack;
    }

    public void setText(TextStack stack) {
        this.stack = stack;
    }

    public abstract Block createBlock();
}
