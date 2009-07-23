/*
 * Change.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

public abstract class Change
{
    protected Change() {}

    /*
     * These could take a context parameter, coming from ChangeStack, if
     * necessary.
     */

    protected abstract void apply();

    protected abstract void undo();

    /**
     * Get the Segment that this Change affects.
     * 
     * @return
     */
    public Segment affects() {
        return null;
    }
}
