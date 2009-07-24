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
package quill.textbase;

import quill.docbook.Block;

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
 * that locally with a Segment[] (or a more complicated data structure as
 * necessary) locally, or if passing it further, wrapping it in a Series.
 * 
 * @author Andrew Cowie
 */
public abstract class Segment
{
    private Series parent;

    protected Segment() {}

    void setParent(Series series) {
        this.parent = series;
    }

    public Series getParent() {
        return parent;
    }

    /**
     * The internal representation of the text body of this Segment.
     */
    private TextChain chain;

    public TextChain getText() {
        return chain;
    }

    public void setText(TextChain stack) {
        this.chain = stack;
        stack.setEnclosingSegment(this);
    }

    public abstract Block createBlock();

    abstract Segment createSimilar();
}
