/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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
package quill.ui;

import java.util.LinkedList;

import quill.textbase.Folio;

/**
 * An ordered list of Folio instances which are the basis of our undo/redo
 * stack. There is one of these for each PrimaryWindow to track modifications
 * to its document.
 * 
 * There is always at least one Folio in the ChangeStack.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO at the moment the depth of the undo list is unlimited; that's cool
 * except that ultimately it will be a memory problem, so someday presumably
 * something will need to act to limit its size. A Queue, perhaps? Or maybe
 * discarding older operations at certain defined lifecycle points?
 */
class ChangeStack
{
    private LinkedList<Folio> stack;

    private int pointer;

    public ChangeStack(Folio start) {
        stack = new LinkedList<Folio>();
        stack.add(0, start);
        pointer = 0;
    }

    /**
     * Add a state to the data layer.
     */
    public void apply(Folio folio) {
        pointer++;

        while (pointer < stack.size()) {
            stack.removeLast();
        }

        stack.add(pointer, folio);
    }

    /**
     * Undo. Return the Folio which represents the state from one before.
     */
    Folio undo() {
        final Folio folio;

        if (pointer == 0) {
            return stack.get(0);
        }

        pointer--;

        folio = stack.get(pointer);

        return folio;
    }

    /**
     * Redo a previous undo. Moves the state forward one.
     */
    Folio redo() {
        final Folio folio;

        if (pointer + 1 == stack.size()) {
            return stack.get(pointer);
        }

        pointer++;

        folio = stack.get(pointer);

        return folio;
    }

    Folio getCurrent() {
        return stack.get(pointer);
    }
}
