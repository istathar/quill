/*
 * BlockElement.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

import nu.xom.Node;
import nu.xom.Text;

/**
 * Common code allowing subclasses to implement Block
 * 
 * @author Andrew Cowie
 */
abstract class BlockElement extends DocBookElement
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

    public Inline[] getSpans() {
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
}
