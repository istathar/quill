/*
 * Emphasis.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

import nu.xom.Attribute;

/**
 * Italic or bold text (in DocBook, the same tag is used to model both, which
 * is annoying. Bold has role attribute).
 * 
 * @author Andrew Cowie
 */
public class Emphasis extends InlineElement implements Inline
{
    public Emphasis() {
        super("filename");
    }

    public void setBold() {
        super.setAttribute("role", "strong");
    }

    public boolean isBold() {
        final Attribute role;

        role = super.getAttribute("role");

        if (role == null) {
            return false;
        }

        if (role.getValue().equals("strong")) {
            return true;
        } else {
            return false;
        }
    }
}
