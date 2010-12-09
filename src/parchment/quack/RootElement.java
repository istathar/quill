/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package parchment.quack;

import nu.xom.Elements;

/**
 * The root element of a Quack format document.
 * 
 * @author Andrew Cowie
 */
public class RootElement extends QuackElement implements Root
{
    public RootElement() {
        super("quack");
    }

    /**
     * Blocks can be added to Components and Divisions.
     */
    public void add(Block block) {
        super.add(block);
    }

    /**
     * Get the children Blocks as an array.
     */
    public Block[] getBlocks() {
        final Elements children;
        int i;
        final int num;
        final Block[] result;

        children = super.getChildElements();

        num = children.size();
        result = new Block[num];

        for (i = 0; i < num; i++) {
            result[i] = (Block) children.get(i);
        }

        return result;
    }
}
