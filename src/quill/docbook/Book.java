/*
 * Book.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * Top level element for a book document.
 * 
 * @author Andrew Cowie
 */
/*
 * This may fade out, or at least be demoted, if we switch to one chapter or
 * article per file max. Which seems likely.
 */
public class Book extends DocBookElement implements Tag
{
    public Book() {
        super("book");
    }

    public void add(Component component) {
        super.add(component);
    }
}
