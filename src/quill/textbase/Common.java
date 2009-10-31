/*
 * Common.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * Common markups that are frequently reused by reference.
 * 
 * In DocBook these are standard block and spanning elements without
 * attributes.
 * 
 * @author Andrew Cowie
 */
public class Common extends Markup
{
    private Common(String variant, boolean spellCheck) {
        super("Common." + variant, spellCheck);
    }

    public static final Common ITALICS = new Common("ITALICS", true);

    public static final Common BOLD = new Common("BOLD", true);

    public static final Common FILENAME = new Common("FILENAME", false);

    public static final Common TYPE = new Common("TYPE", false);

    public static final Common LITERAL = new Common("CODE", false);

    public static final Common FUNCTION = new Common("FUNCTION", false);

    public static final Common APPLICATION = new Common("APPLICATION", true);

    public static final Common COMMAND = new Common("COMMAND", false);
}
