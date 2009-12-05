/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2009 Operational Dynamics Consulting, Pty Ltd
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

/**
 * Discrete operations that can be applied to change a Series.
 * 
 * @author Andrew Cowie
 */
public abstract class StructuralChange extends Change
{
    final int index;

    final Segment into;

    final int offset;

    final Segment added;

    StructuralChange(Segment into, int offset, Segment added) {
        super(into);

        final Series series;
        int i;

        series = into.getParent();
        this.into = into;
        this.offset = offset;

        added.setParent(series);
        this.added = added;

        for (i = 0; i < series.size(); i++) {
            if (series.get(i) == into) {
                this.index = i;
                return;
            }
        }

        throw new IllegalStateException();
    }

    public Segment getInto() {
        return into;
    }

    /**
     * Offset into <code>into</code> that the split occured. Special cases are
     * <code>0</code> which indicates an prepend at the beginning of the
     * <code>into</code> Segment, and <code>-1</code> as the marker that an
     * append after happened.
     */
    public int getOffset() {
        return offset;
    }

    public Segment getAdded() {
        return added;
    }

    // public Segment getTwain() {
    // return twain;
    // }
}
