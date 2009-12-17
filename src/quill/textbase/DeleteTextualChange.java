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
 * Remove a range of text.
 * 
 * @author Andrew Cowie
 */
public class DeleteTextualChange extends TextualChange
{
    /**
     * While it would be nice to be able to say
     * "just remove between these two points", we need to have a list of Spans
     * that are going to be removed so we can restore them later if an undo
     * happens.
     */
    public DeleteTextualChange(TextChain chain, int offset, Extract removed) {
        super(chain, offset, removed, null);
    }

    protected void apply() {
        chain.delete(offset, removed.getWidth());
    }

    protected void undo() {
        chain.insert(offset, removed);
    }
}
