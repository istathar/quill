/*
 * ImageElement.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;


public class ImageElement extends BlockElement implements Block
{
    public ImageElement() {
        super("image");
    }

    public void add(String text) {
        super.setValue("src", text);
    }

    // yikes
    public Inline[] getBody() {
        final Inline[] result;
        final String src;

        result = new Inline[1];

        src = super.getValue("src");
        result[0] = new Normal(src);

        return result;
    }
}
