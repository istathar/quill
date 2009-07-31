/*
 * Paragraph.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * We model Paragraphs that do not contain block level elements. Paragraphs
 * are unusual (and annoying) in that they can be contained in other blocks.
 * 
 * @author Andrew Cowie
 */
/*
 * FIXME Paragraphs are special. They may need to be turned into a unique
 * type.
 */
public class Paragraph extends BlockElement implements Block
{
    public Paragraph() {
        super("para");
    }
}
