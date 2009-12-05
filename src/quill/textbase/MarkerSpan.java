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
 * A special Span marking the position of a "marker", presented in the UI as
 * something 1 character wide but actually displayed by a heavy Widget and not
 * a character in the TextView.
 * 
 * @author Andrew Cowie
 */
public class MarkerSpan extends Span
{
    final String reference;

    public MarkerSpan(String reference, Markup markup) {
        super(markup);
        this.reference = reference;
    }

    protected Span copy(Markup markup) {
        return this; // ?
    }

    public String getText() {
        return reference;
    }

    public int getChar(int position) {
        return '☢';
    }

    public int getWidth() {
        return 1;
    }

    // does this matter?
    Span split(int begin, int end) {
        if ((begin != 0) || (end != 1)) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    Span split(int begin) {
        if (begin != 0) {
            throw new IllegalArgumentException();
        }
        return this;
    }
}
