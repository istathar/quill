/*
 * InlineElement.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
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
