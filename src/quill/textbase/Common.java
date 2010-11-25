/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.textbase;

/**
 * Common markups that are frequently reused by reference.
 * 
 * In DocBook these are standard block and spanning elements without
 * attributes.
 * 
 * @author Andrew Cowie
 */
public class Common extends Markup
{
    private Common(String variant, boolean spellCheck) {
        super("Common." + variant, spellCheck);
    }

    public static final Common ITALICS = new Common("ITALICS", true);

    public static final Common BOLD = new Common("BOLD", true);

    public static final Common FILENAME = new Common("FILENAME", false);

    public static final Common TYPE = new Common("TYPE", false);

    public static final Common LITERAL = new Common("CODE", false);

    public static final Common FUNCTION = new Common("FUNCTION", false);

    public static final Common NAME = new Common("APPLICATION", false);

    public static final Common COMMAND = new Common("COMMAND", false);

    public static final Common HIGHLIGHT = new Common("HIGHLIGHT", false);

    public static final Common PUBLICATION = new Common("PUBLICATION", true);

    public static final Common KEYBOARD = new Common("KEYBOARD", false);

    public static final Common ACRONYM = new Common("PROPER", false);
}
