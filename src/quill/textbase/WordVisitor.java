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
 * Visit the words in range, one by one.
 * 
 * @author Andrew Cowie
 */
public interface WordVisitor
{
    /**
     * The two offsets are in characters which saves you having to worry about
     * the encoding used by String. Return <code>true</code> if you are done
     * visiting and want to stop callbacks.
     * 
     * @param word
     *            The next word in the range
     * @param skip
     *            Should we actually skip spell checking this word (based on
     *            it containing a Markup that is marked not to be checked?)
     * @param begin
     *            The offset in the tree where this word started
     * @param end
     *            The offset in the tree where this word ends
     */
    /*
     * Offsets are in terms of the tree on which the original Node's visit()
     * call was invoked, not the present enclosing node; the word may well be
     * longer than a single Node.
     */
    public boolean visit(String word, boolean skip, int begin, int end);
}
