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

import nu.xom.Attribute;
import nu.xom.Node;
import nu.xom.Text;

/**
 * Common code allowing subclasses to implement Block
 * 
 * @author Andrew Cowie
 */
abstract class BlockElement extends QuackElement
{
    BlockElement(String name) {
        super(name);
    }

    public void add(String text) {
        super.add(text);
    }

    public void add(Inline markup) {
        super.add(markup);
    }

    public void add(Meta data) {
        super.add(data);
    }

    public Inline[] getBody() {
        final int num;
        final Inline[] result;
        int i;
        Node child;

        num = super.getChildCount();
        result = new Inline[num];

        for (i = 0; i < num; i++) {
            child = super.getChild(i);

            if (child instanceof Text) {
                result[i] = new Normal(child);
            } else if (child instanceof Inline) {
                result[i] = (Inline) child;
            } else {
                throw new IllegalStateException("\n" + "What is " + child);
            }
        }

        return result;
    }

    public Meta[] getData() {
        final int len;
        final Meta[] result;
        int i;
        Attribute a;

        len = super.getAttributeCount();
        result = new Meta[len];

        for (i = 0; i < len; i++) {
            a = super.getAttribute(i);

            if (a instanceof Meta) {
                result[i] = (Meta) a;
            } else {
                throw new IllegalStateException("\n" + "Can't handle non Meta attributes! " + a.toXML());
            }
        }

        return result;
    }
}
