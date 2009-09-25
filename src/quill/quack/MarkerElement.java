/*
 * MarkerElement.java
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
 * Common implementation for Marker classes
 * 
 * @author Andrew Cowie
 */
abstract class MarkerElement extends QuackElement
{
    MarkerElement(String name) {
        super(name);
    }

    public void add(String text) {
        super.setValue("ref", text);
    }

    public String getText() {
        return super.getValue("ref");
    }
}
