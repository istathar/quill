/*
 * ImageSegment.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

public class ImageSegment extends Segment
{
    public ImageSegment() {
        super();
    }

    Segment createSimilar() {
        final Segment result;

        result = new ImageSegment();
        result.setParent(this.getParent());

        return result;
    }

}
