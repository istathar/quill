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
package markerpen.textbase;

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
    private Common() {
        super();
    }

    public static final Common ITALICS = new Common();

    public static final Common BOLD = new Common();

    public static final Common FILENAME = new Common();
}
