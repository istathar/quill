/*
 * TitleElement.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

public class TitleElement extends BlockElement implements Block
{
    public TitleElement() {
        super("title");
    }

    public TitleElement(String title) {
        super("title");
        super.add(title);
    }
}
