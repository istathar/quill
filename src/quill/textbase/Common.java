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
    private Common(String variant) {
        super("Common." + variant);
    }

    public static final Common ITALICS = new Common("ITALICS");

    public static final Common BOLD = new Common("BOLD");

    public static final Common FILENAME = new Common("FILENAME");

    public static final Common TYPE = new Common("TYPE");

    public static final Common CODE = new Common("CODE");

    public static final Common FUNCTION = new Common("FUNCTION");

    public static final Common APPLICATION = new Common("APPLICATION");

    public static final Common COMMAND = new Common("COMMAND");
}
