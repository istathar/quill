/*
 * Changeable.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import quill.textbase.Change;

/**
 * Mark a user interface element as one whose underlying data structures can
 * be mutated by Change objects.
 * 
 * @author Andrew Cowie
 */
interface Changeable
{
    void affect(Change change);

    void reverse(Change change);
}
