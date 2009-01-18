/*
 * MetaSpan.java
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
 * A Span with metadata
 * 
 * @author Andrew Cowie
 * @author Devdas Bhagat
 */
abstract class MetaSpan extends Span
{
    public String getText() {
        return "Δ";
    }
}
