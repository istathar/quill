/*
 * SourceAttribute.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

/**
 * Attributes which describe the location of external files, for instance
 * <code>src="images/OldMap.jpg"</code>.
 * 
 * @author Andrew Cowie
 */
public class SourceAttribute extends QuackAttribute implements Meta
{
    SourceAttribute(String value) {
        super("src", value);
    }
}
