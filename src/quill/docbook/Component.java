/*
 * Component.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * Components are the chapter-like elements.
 */
public interface Component extends Tag
{
    public void add(Division section);

    /**
     * Less common is adding block level elements to a Chapter. These occur
     * before the first Section. Mostly this is how a Title gets added.
     */
    public void add(Block block);

    public Division[] getDivisions();
}
