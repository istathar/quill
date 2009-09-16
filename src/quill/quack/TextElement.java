/*
 * TextElement.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.quack;

/**
 * A single paragraph of normal text.
 * 
 * @author Andrew Cowie
 */
/*
 * This class is regretablly closely named to [nu.xom] Text which is used for
 * Strings nodes in XOM. However, we've gone with <text> rather than <para> in
 * Quack to emphasize that there are various ways to make paragraphs. So
 * TextElement it is.
 */
public class TextElement extends BlockElement implements Block
{
    public TextElement() {
        super("text");
    }
}
