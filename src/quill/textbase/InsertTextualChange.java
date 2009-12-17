/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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
 * Insert some text into the TextChain.
 * 
 * @author Andrew Cowie
 */
/*
 * FUTURE wouldn't we be better off having an InsertTextualChange that works
 * for just a single Span? Admittedly that's only for keystrokes, but
 * keystrokes are everything in a word processor! Maybe the whole question is
 * moot if we get rid of Changes...
 */
public class InsertTextualChange extends TextualChange
{
    /**
     * This is the usual case: you've created a single Span and want to insert
     * it.
     */
    public InsertTextualChange(TextChain chain, int offset, Span span) {
        super(chain, offset, null, Node.create(span));
    }

    /**
     * Alternately, you've been given a range from somewhere and you want to
     * (re)insert it.
     */
    public InsertTextualChange(TextChain chain, int offset, Node added) {
        super(chain, offset, null, added);
    }

    public void apply() {
        chain.insert(offset, added);
    }

    public void undo() {
        chain.delete(offset, added.getWidth());
    }
}
