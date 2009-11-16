/*
 * SafelyTerminateException.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.client;

/**
 * Propegate this to silently terminate the application. Use this is instead
 * of System.exit() for unusual but not abnormal shutdown.
 * 
 * @author Andrew Cowie
 */
@SuppressWarnings("serial")
public class SafelyTerminateException extends ApplicationException
{
    public SafelyTerminateException() {
        super(null);
    }
}
