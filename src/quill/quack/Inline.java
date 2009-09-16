/*
 * Inline.java
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
 * Tags that are presented as spans within Blocks. Note that we don't allow
 * Inlines to be nested inside other Inlines.
 * 
 * @author Andrew Cowie
 */
public interface Inline extends Tag
{
    public void add(String str);

    public String getText();
}
