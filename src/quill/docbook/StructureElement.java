/*
 * StructureElement.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * Structural elemtns are things you can add Blocks too.
 * 
 * @author Andrew Cowie
 */
abstract class StructureElement extends DocBookElement
{
    StructureElement(String name) {
        super(name);
    }

    /**
     * Blocks can be added to Components and Divisions.
     */
    public void add(Block block) {
        super.add(block);
    }
}
