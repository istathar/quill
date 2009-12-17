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
 * A change to the character content of the TextChain. This can be an
 * insertion, a deletion, or a replacement (which is both simultaneously).
 * 
 * @author Andrew Cowie
 */
public class FullTextualChange extends TextualChange
{
    /**
     * Insert a Span[] at offset
     */
    public FullTextualChange(TextChain chain, int offset, Node added) {
        super(chain, offset, null, added);
    }

    /**
     * Replace the text between offset and width with a new Span[].
     */
    public FullTextualChange(TextChain chain, int offset, Node replaced, Node added) {
        super(chain, offset, replaced, added);
    }

    /**
     * Replace the text between offset and the width of replaced with the
     * given Span.
     */
    public FullTextualChange(TextChain chain, int offset, Node replaced, Span span) {
        super(chain, offset, replaced, Node.create(span));
    }

    protected void apply() {
        if (removed == null) {
            chain.insert(offset, added);
        } else if (added == null) {
            chain.delete(offset, removed.getWidth());
        } else {
            chain.delete(offset, removed.getWidth());
            chain.insert(offset, added);
        }
    }

    protected void undo() {
        if (removed == null) {
            chain.delete(offset, added.getWidth());
        } else if (added == null) {
            chain.insert(offset, removed);
        } else {
            chain.delete(offset, added.getWidth());
            chain.insert(offset, removed);
        }
    }
}
