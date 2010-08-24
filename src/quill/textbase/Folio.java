/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import parchment.format.Chapter;
import parchment.format.Manuscript;

/**
 * A sequence of Series making up a document.
 * 
 * <p>
 * In the case of an article there will be exactly one Series. In the case of
 * a book, there will be a sequence of one or more Series.
 * 
 * @author Andrew Cowie
 */
// immutable
public class Folio
{
    private final Series[] components;

    private final Chapter[] chapters;

    /**
     * The file [reference] that this document was (orignally) loaded from.
     * While Folio states evolve, importantly the Manuscript will stay the
     * same unless and until the underlying filename etc is changed.
     */
    private final Manuscript manuscript;

    /**
     * Create a Folio with a single [presumably nigh-on-empty] component as
     * its body.
     */
    public Folio(Manuscript manuscript, Chapter chapter, Series component) {
        this.manuscript = manuscript;
        this.chapters = new Chapter[] {
            chapter
        };
        this.components = new Series[] {
            component
        };
    }

    /**
     * Create a Folio with a list of components to be used as the body.
     */
    public Folio(Manuscript manuscript, List<Chapter> chapters, List<Series> components) {
        final int num;
        final Chapter[] c;
        final Series[] s;

        num = chapters.size();
        if (num != components.size()) {
            throw new AssertionError();
        }

        this.manuscript = manuscript;

        c = new Chapter[num];
        this.chapters = chapters.toArray(c);

        s = new Series[num];
        this.components = components.toArray(s);
    }

    private Folio(Manuscript manuscript, Chapter[] chapters, Series[] components) {
        this.manuscript = manuscript;
        this.chapters = chapters;
        this.components = components;
    }

    public int size() {
        return components.length;
    }

    /**
     * Get the Manuscript object representing the file this Folio was loaded
     * from, or will be saved to.
     */
    public Manuscript getManuscript() {
        return manuscript;
    }

    public Series getSeries(int index) {
        return components[index];
    }

    /**
     * Get the [reference to] the Chapter on disk at position <code>i</code>.
     */
    public Chapter getChapter(int index) {
        return chapters[index];
    }

    public int indexOf(Series series) {
        int i;

        for (i = 0; i < components.length; i++) {
            if (components[i] == series) {
                return i;
            }
        }

        throw new IllegalArgumentException("\n" + "Series not in this Folio");
    }

    public Folio update(int position, Series series) {
        final Series[] original, replacement;

        original = this.components;

        replacement = new Series[original.length];

        System.arraycopy(original, 0, replacement, 0, position);
        replacement[position] = series;
        System.arraycopy(original, position + 1, replacement, position + 1, original.length - position
                - 1);

        return new Folio(this.manuscript, this.chapters, replacement);
    }
}
