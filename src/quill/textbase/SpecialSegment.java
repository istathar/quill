/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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
package quill.textbase;

public final class SpecialSegment extends Segment
{
    public SpecialSegment(String type) {
        super(Extract.create(), validate(type), 0, 0, 0);
    }

    /**
     * We can't enforce the three known valid values for type in the DTD, so
     * check it here instead.
     */
    private static String validate(String type) {
        if (type.equals("endnotes")) {
            return type;
        } else if (type.equals("references")) {
            return type;
        } else if (type.equals("contents")) {
            return type;
        } else {
            throw new IllegalArgumentException("Unknown type \"" + type + "\"");
        }
    }

    /**
     * @param entire
     */
    private SpecialSegment(Extract entire, String type, int offset, int removed, int inserted) {
        super(entire, type, offset, removed, inserted);
    }

    public Segment createSimilar(Extract entire, int offset, int removed, int inserted) {
        return new SpecialSegment(entire, super.getImage(), offset, removed, inserted);
    }
}
