/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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

import java.util.List;

/**
 * A sequence of Series making up a document.
 * 
 * <p>
 * In the case of an article there will be exactly one Series. In the case of
 * a book, there will be a sequence of one or more Series.
 * 
 * @author Andrew Cowie
 */
public class Folio
{
    private final Series[] collection;

    Folio(Series[] components) {
        this.collection = components;
    }

    /**
     * Create a Folio with a single [presumably nigh-on-empty] chapter as its
     * body.
     */
    public static Folio create(Series component) {
        return new Folio(new Series[] {
            component
        });
    }

    public static Folio create(List<Series> components) {
        Series[] array;

        array = new Series[components.size()];
        components.toArray(array);

        return new Folio(array);
    }

    public int size() {
        return collection.length;
    }

    public Series get(int index) {
        return collection[index];
    }
}
