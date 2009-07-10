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

    final int index;

    final Segment into;

    final int offset;

    final Segment added;

    StructuralChange(Series series, Segment into, int offset, Segment added) {
        int i;

        this.series = series;
        this.into = into;
        this.offset = offset;
        this.added = added;

        for (i = 0; i < series.size(); i++) {
            if (series.get(i) == into) {
                this.index = i;
                return;
            }
        }

        throw new IllegalStateException();
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
