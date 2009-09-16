/*
 * Block.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

/**
 * A body of text that contains inlines. This is either a single paragraph in
 * normal text, or a continuous listing of source code.
 * 
 * @author Andrew Cowie
 */
public interface Body extends Tag
{
    /*
     * Nothing wrong with packing normal text into a Block. Usually that'll be
     * Paragraph anyway.
     */

    public Inline[] getSpans();
}
