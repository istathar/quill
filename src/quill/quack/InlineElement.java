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
package quill.quack;

import nu.xom.Node;
import nu.xom.Text;

/**
 * Common implementation for Inline classes
 * 
 * @author Andrew Cowie
 */
abstract class InlineElement extends QuackElement
{
    InlineElement(String name) {
        super(name);
    }

    public void add(String text) {
        super.add(text);
    }

    public String getText() {
        final int num;
        final Node child;
        final Text text;

        num = this.getChildCount();
        if (num != 1) {
            throw new IllegalStateException("How did you get an Inline without a single child?");
        }

        child = this.getChild(0);
        if (!(child instanceof Text)) {
            throw new IllegalStateException("How did you get an Inline without a text body?");
        }
        text = (Text) child;

        return text.getValue();
    }
}
