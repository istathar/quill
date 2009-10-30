/*
 * TypeElement.java
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
 * A type or class name.
 * 
 * @author Andrew Cowie
 */
public class TypeElement extends InlineElement implements Inline
{
    public TypeElement() {
        super("type");
    }
}