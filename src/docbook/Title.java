/*
 * Title.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

public class Title extends BlockTag
{
    public Title() {
        super("title");
    }

    public Title(String title) {
        super("title");
        super.addText(title);
    }
}
