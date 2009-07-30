/*
 * Division.java
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
 * The grouping level below Component and above Block. This is what Sections
 * belong to.
 * 
 * @author Andrew Cowie
 */
/*
 * The documentation sometimes refers to this as "element", but Element is
 * already horribly over used, so we'll use this as something out of the way.
 */
public interface Division extends Tag
{
    public void add(Block block);

    public Block[] getBlocks();
}
