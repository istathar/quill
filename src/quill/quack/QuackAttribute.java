/*
 * QuackAttribute.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

import nu.xom.Attribute;

/**
 * Internal root class for all of our metadata attributes
 * 
 * @author Andrew Cowie
 */
abstract class QuackAttribute extends Attribute
{
    QuackAttribute(String name, String value) {
        /*
         * For some reason, we don't pass the quack URI when constructing
         * quack attributes. I don't understand why that would be the case.
         */
        super(name, value);
    }
}
