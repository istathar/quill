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

/**
 * Read-only wrapper around an array of Spans.
 * 
 * @author Andrew Cowie
 */
/*
 * The fields are package visible and that's ok because in here we know not to
 * change Span[], but if an Extract escapes outside this package then the
 * public methods are available and this class is immutable.
 */
public class Extract
{
    final Span[] range;

    final int width;

    Extract(Span[] range) {
        int w;

        w = 0;

        for (Span s : range) {
            w += s.getWidth();
        }

        this.width = w;
        this.range = range;
    }

    Extract(Span span) {
        this.width = span.getWidth();
        this.range = new Span[] {
            span
        };
    }

    Extract() {
        this.width = 0;
        this.range = new Span[] {};
    }

    /**
     * Get the number of Spans in this Range.
     */
    public int size() {
        return range.length;
    }

    /**
     * Get the Span at index. Don't exceed {@link #size()} - 1.
     */
    public Span get(int index) {
        return range[index];
    }

    /**
     * Get the width, in characeters, that the Spans in this Range represent.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get a single continuous String with the textual contents of this
     * Extract. The only time you should need this is for writing to
     * clipboard.
     */
    public String getText() {
        final StringBuilder str;
        int i;

        str = new StringBuilder();

        for (i = 0; i < range.length; i++) {
            str.append(range[i].getText());
        }

        return str.toString();
    }

    /**
     * For debugging, only!
     */
    public String toString() {
        final StringBuilder str;
        int i;

        str = new StringBuilder();

        for (i = 0; i < range.length; i++) {
            str.append(range[i].toString());
            str.append('\n');
        }

        return str.toString();
    }
}
