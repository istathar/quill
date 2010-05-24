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
package quill.textbase;

import java.util.LinkedList;

/**
 * An ordered list of Change instances which are the basis of our undo/redo
 * stack. There is one of these for each PrimaryWindow to track modifications
 * to its document.
 * 
 * Although public, it is only so in order that PrimaryWindow can see it; it
 * is in the textbase package in order that the restricted action methods on
 * Change objects can be accessed.
 * 
 * @author Andrew Cowie
 */
/*
 * TODO at the moment the depth of the undo list is unlimited; that's cool
 * except that ultimately it will be a memory problem, so someday presumably
 * something will need to act to limit its size. A Queue, perhaps? Or maybe
 * discarding older operations at certain defined lifecycle points?
 */
public class ChangeStack
{
    private LinkedList<Change> stack;

    private int pointer;

    public ChangeStack() {
        stack = new LinkedList<Change>();
        pointer = 0;
    }

    /**
     * Apply a Change to the data layer.
     */
    public void apply(Change change) {
        while (pointer < stack.size()) {
            stack.removeLast();
        }

        stack.add(pointer, change);
        pointer++;

        change.apply();
    }

    /**
     * Undo. Return the Change which represents the delta from current to one
     * before.
     */
    public Change undo() {
        final Change change;

        if (stack.size() == 0) {
            return null;
        }
        if (pointer == 0) {
            return null;
        }
        pointer--;

        change = stack.get(pointer);
        change.undo();

        return change;
    }

    /**
     * Redo a previous undo. Returns the Change which is the delta you will
     * need to [re]apply.
     */
    public Change redo() {
        final Change change;

        if (stack.size() == 0) {
            return null;
        }
        if (pointer == stack.size()) {
            return null;
        }

        change = stack.get(pointer);
        change.apply();

        pointer++;

        return change;
    }

    public Change getCurrent() {
        if (pointer == 0) {
            return null;
        } else {
            return stack.get(pointer - 1);
        }
    }
}
