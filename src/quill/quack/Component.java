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
package quill.quack;

/**
 * Components are the chapter-like elements. They contain a series of Blocks.
 */
public interface Component extends Tag
{
    public void add(Block block);

    public Block[] getBlocks();
}
