/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.textbase;

/**
 * Segments are the block level grouping mechanism in textbase. Concrete
 * instances of this represent the entities we model as UI blocks each with
 * their own customized EditorWidget.
 * 
 * <p>
 * Note that some of these logical structures may correspond to more than one
 * underlying XML block level element; see {@link NormalSegment}.
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
    protected Segment() {}

    /**
     * The internal representation of the text body of this Segment.
     */
    private TextChain chain;

    public TextChain getText() {
        return chain;
    }

    public void setText(TextChain chain) {
        this.chain = chain;
        chain.setEnclosingSegment(this);
    }

    abstract Segment createSimilar();

    private String image;

    /*
     * This is called by RenderEngine...
     */
    public String getImage() {
        return image;
    }

    public void setImage(String filename) {
        this.image = filename;
    }
}
