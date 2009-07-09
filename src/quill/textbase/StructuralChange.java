/*
 * TextualChange.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

/**
 * Discrete operations that can be applied to change a Series.
 * 
 * @author Andrew Cowie
 */
public abstract class StructuralChange extends Change
{
    final Series series;

    final Segment into;

    final int offset;

    final Segment added;

    StructuralChange(Series series, Segment into, int offset, Segment added) {
        this.series = series;
        this.into = into;
        this.offset = offset;
        this.added = added;
    }

    public int getOffset() {
        return offset;
    }

    public Series getOriginal() {
        return null;
    }

    public Segment getAdded() {
        return null;
    }
}
