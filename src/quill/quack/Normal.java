/*
 * Normal.java
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
