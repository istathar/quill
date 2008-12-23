/*
 * Italics.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

public class Italics extends Inline
{
    public Italics() {
        super("emphasis");
    }

    public Italics(String str) {
        super("emphasis");
        super.addText(str);
    }
}
