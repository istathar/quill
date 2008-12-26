/*
 * DeltaText.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package textbase;

import java.util.LinkedList;

/**
 * A Text clas that is mutated by applying Change instances, which in turn are
 * the basis of our undo/redo stack.
 * 
 * @author Andrew Cowie
 */
public class TextStack extends Text
{
    private LinkedList<Change> stack;

    private int pointer;

    public TextStack() {
        super("");
        stack = new LinkedList<Change>();
        pointer = 0;
    }

    public void apply(Change change) {
        while (pointer < stack.size()) {
            stack.removeLast();
        }

        stack.add(pointer, change);
        pointer++;

        change.apply(this);
    }

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
        change.undo(this);

        return change;
    }

    public Change redo() {
        final Change change;

        if (stack.size() == 0) {
            return null;
        }
        if (pointer == stack.size()) {
            return null;
        }

        change = stack.get(pointer);
        change.apply(this);

        pointer++;

        return change;
    }
}
