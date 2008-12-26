/*
 * AllTests.java
 *
 * Copyright (c) 2005-2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 * 
 * Based on the java-gnome UnitTests suite, which in turn was imported from
 * the UnitTests suite in objective.
 */
package com.operationaldynamics.textbase;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.operationaldynamics.junit.VerboseTestRunner;

public class AllTests
{
    /**
     * Entry point from the command line, of course. Uses VerboseTestRunner to
     * do a more pretty printing of the test output.
     */
    public static void main(String[] args) {
        VerboseTestRunner.run(suite(args));
    }

    /**
     * Entry point used by Eclipse's built in JUnit TestRunner
     */
    public static Test suite() {
        return suite(null);
    }

    private static Test suite(String[] args) {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        TestSuite suite = new TestSuite("All Unit Tests for com.operationaldynamics.textbase");

        suite.addTestSuite(ValidateChunks.class);
        suite.addTestSuite(ValidateText.class);
        suite.addTestSuite(ValidateApplyUndoRedo.class);

        return suite;
    }
}
