/*
 * Command.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.docbook;

/**
 * A command or program name, ie as you would run it from the command line.
 * 
 * @author Andrew Cowie
 */
public class Command extends Inline
{
    public Command() {
        super("command");
    }
}
