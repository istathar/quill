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
package parchment.quack;

import nu.xom.Node;
import nu.xom.Text;

/**
 * Normal text, modelled as an Inline. This is a delegate wrapper around XOM's
 * Text, but unlike Element subclasses we do not insert these into the tree.
 * It's just here so we can use it in Block's getBody().
 * 
 * @author Andrew Cowie
 */
/*
 * This needs a better name.
 */
public class Normal implements Inline
{
    private final String cached;

    public Normal(Node node) {
        final Text text;

        text = (Text) node;
        cached = text.getValue();
    }

    public Normal(String str) {
        cached = str;
    }

    public void add(String str) {
        throw new UnsupportedOperationException("Fake stub");
    }

    public String getText() {
        return cached;
    }

    public String toString() {
        return "(text)";
    }
}
