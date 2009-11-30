/*
 * ChangeStack.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.util.LinkedList;

/**
 * An ordered list of Change instances which are the basis of our undo/redo
 * stack. This is delegated to by DataLayer.
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
    private LinkedList<Change> stack;

    private int pointer;

    ChangeStack() {
        stack = new LinkedList<Change>();
        pointer = 0;
    }

    /**
     * Apply a Change to the data layer.
     */
    void apply(Change change) {
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
    Change undo() {
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
    Change redo() {
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

    Change getCurrent() {
        if (pointer == 0) {
            return null;
        } else {
            return stack.get(pointer - 1);
        }
    }
}
