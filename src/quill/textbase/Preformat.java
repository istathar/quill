/*
 * Preformat.java
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
 * Common markups that are frequently reused by reference that relate to
 * constant-width blocks.
 * 
 * <p>
 * In DocBook these are standard block and spanning elements without
 * attributes such as <code>&lt;programlisting&gt;</code>.
 * 
 * @author Andrew Cowie
 */
public class Preformat extends Markup
{
    private Preformat(String variant, boolean spellCheck) {
        super("Preformat." + variant, spellCheck);
    }

    public static final Preformat USERINPUT = new Preformat("USERINPUT", false);
}
