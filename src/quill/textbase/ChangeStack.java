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
 * stack.
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
    private LinkedList<TextualChange> stack;

    private int pointer;

    public ChangeStack() {
        stack = new LinkedList<TextualChange>();
        pointer = 0;
    }

    public void apply(TextualChange change) {
        while (pointer < stack.size()) {
            stack.removeLast();
        }

        stack.add(pointer, change);
        pointer++;

        change.apply(fixme);
    }

    public TextualChange undo() {
        final TextualChange change;

        if (stack.size() == 0) {
            return null;
        }
        if (pointer == 0) {
            return null;
        }
        pointer--;

        change = stack.get(pointer);
        change.undo(fixme);

        return change;
    }

    public TextualChange redo() {
        final TextualChange change;

        if (stack.size() == 0) {
            return null;
        }
        if (pointer == stack.size()) {
            return null;
        }

        change = stack.get(pointer);
        change.apply(fixme);

        pointer++;

        return change;
    }
}
