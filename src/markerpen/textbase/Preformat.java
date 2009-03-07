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
package markerpen.textbase;

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
    private Preformat(String variant) {
        super("Preformat." + variant);
    }

    /*
     * Unlike the Parapgraph case which is currently assumed for markup =
     * null, we need a way to signal ProgramListing blocks.
     */
    public static final Preformat NORMAL = new Preformat("NORMAL");

    public static final Preformat USERINPUT = new Preformat("USERINPUT");
}