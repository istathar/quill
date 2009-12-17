/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
 * A range (or all) of the text within a TextChain.
 * 
 * @author Andrew Cowie
 */
/*
 * This base class would be an interface except it allows us to have a public
 * API with a create() function in it, and to adjust implementations down the
 * track. It's not a base class for a binary-tree; if you need one do that in
 * the Node subclass.
 */
public abstract class Extract
{
    /**
     * Get the width of this range, in characters.
     */
    public abstract int getWidth();

    /**
     * Get a String representing [just] the text in this range.
     */
    /*
     * Only call this for cases where you only need a concatonated String; if
     * you're doing anything more interesting then visit over the tree.
     */
    public abstract String getText();

    /**
     * Create an Extract wrapping the given Span.
     */
    public static Extract create(Span span) {
        return Node.createNode(span);
    }
}
