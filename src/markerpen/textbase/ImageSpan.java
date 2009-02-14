/*
 * ImageSpan.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

/**
 * A placeholder for an image.
 * 
 * Images are a bit special in that they are one characted wide in
 * TextBuffers; they could be zero characters wide in textbase, but they doing
 * it this way means we have offset consistency between TextBuffer and Chain.
 * 
 * The metadata relating to positioning such as size information goes in an
 * {@link Frame} that should be placed at this location's markup.
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
/*
 * Make this a Markup instead?
 */
public class ImageSpan extends Span
{
    /*
     * This could become a richer type if more than a single relative path or
     * whatever is needed.
     */
    private String ref;

    /*
     * TODO: image type?
     */
    public ImageSpan(String ref, Markup markup) {
        super(markup);
        this.ref = ref;
    }

    public String getText() {
        return "";
    }

    public char getChar() {
        return 0;
    }

    /*
     * Do we actually want to expose in this fashion.
     */
    public String getRef() {
        return ref;
    }

    /*
     * As stored in a TextBuffer, Images and Widgets take 1 char. We return
     * this value so that the TextBuffer : TextChain offset ratios remain
     * equal.
     */
    public int getWidth() {
        return 1;
    }

    protected Span copy(Markup markup) {
        return new ImageSpan(this.ref, markup);
    }
}
