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
 * Visit the characters in a range, one by one, in order.
 * 
 * @author Andrew Cowie
 */
public interface CharacterVisitor
{
    /**
     * Callback for each character. Return <code>false</code> to keep going,
     * or <code>true</code> to say you're done visiting.
     * 
     * @param character
     *            the Unicode codepoint at this offset
     * @param markup
     *            the Markup formatting applicable at this offset.
     */
    public boolean visit(int character, Markup markup);
}
