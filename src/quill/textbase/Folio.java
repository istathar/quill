/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2011 Operational Dynamics Consulting, Pty Ltd
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

import parchment.manuscript.Chapter;
import parchment.manuscript.Manuscript;
import parchment.manuscript.Metadata;
import parchment.manuscript.Stylesheet;

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
    private final Component[] components;

    /**
     * Which Series was updated to create this Folio?
     */
    private final int updated;

    /**
     * The current settings regarding the presentation of this document.
     */
    private final Stylesheet style;

    /**
     * The current metadata about this document.
     */
    private final Metadata meta;

    /**
     * The file [reference] that this document was (orignally) loaded from.
     * While Folio states evolve, importantly the Manuscript will stay the
     * same unless and until the underlying filename etc is changed.
     */
    private final Manuscript manuscript;

    /**
     * The file [reference] that each of the components was loaded from.
     */
    private final Chapter[] chapters;

    /**
     * Create a Folio with a single [presumably nigh-on-empty] component as
     * its body.
     */
    public Folio(Manuscript manuscript, Chapter chapter, Component component, Stylesheet style,
            Metadata meta) {
        this.manuscript = manuscript;
        this.chapters = new Chapter[] {
            chapter
        };
        this.components = new Component[] {
            component
        };
        this.updated = -1;
        this.style = style;
        this.meta = meta;
    }

    /**
     * Create a Folio with a list of components to be used as the body.
     */
    public Folio(Manuscript manuscript, List<Chapter> chapters, List<Component> components,
            Stylesheet style, Metadata meta) {
        final int num;
        final Chapter[] c;
        final Component[] s;

        num = chapters.size();
        if (num != components.size()) {
            throw new AssertionError();
        }

        this.manuscript = manuscript;

        c = new Chapter[num];
        this.chapters = chapters.toArray(c);

        s = new Component[num];
        this.components = components.toArray(s);
        this.updated = -1;
        this.style = style;
        this.meta = meta;
    }

    private Folio(Manuscript manuscript, Chapter[] chapters, Component[] components, int updated,
            Stylesheet style, Metadata meta) {
        this.manuscript = manuscript;
        this.chapters = chapters;
        this.components = components;
        this.updated = updated;
        this.style = style;
        this.meta = meta;
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

    /**
     * Get the <code>i</code>th Component in this Folio.
     */
    public Component getComponent(int index) {
        return components[index];
    }

    /**
     * Get the [reference to] the Chapter on disk at position <code>i</code>.
     */
    public Chapter getChapter(int index) {
        return chapters[index];
    }

    public int indexOf(Component component) {
        int i;

        for (i = 0; i < components.length; i++) {
            if (components[i] == component) {
                return i;
            }
        }

        throw new IllegalArgumentException("\n" + "Component not in this Folio");
    }

    public Folio update(int position, Component component) {
        final Component[] original, replacement;

        original = this.components;

        replacement = new Component[original.length];

        System.arraycopy(original, 0, replacement, 0, position);
        replacement[position] = component;
        System.arraycopy(original, position + 1, replacement, position + 1, original.length - position
                - 1);

        return new Folio(this.manuscript, this.chapters, replacement, position, this.style, this.meta);
    }

    public int getIndexUpdated() {
        return updated;
    }

    public Stylesheet getStylesheet() {
        return style;
    }

    public Metadata getMetadata() {
        return meta;
    }

    public Folio update(Stylesheet style) {
        return new Folio(this.manuscript, this.chapters, this.components, -1, style, this.meta);
    }

    public Folio update(Metadata meta) {
        return new Folio(this.manuscript, this.chapters, this.components, -1, this.style, meta);
    }
}
