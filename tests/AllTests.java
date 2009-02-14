/*
 * AllTests.java (for the markerpen application)
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import markerpen.converter.ValidateStackToDocBookConversion;
import markerpen.textbase.ValidateApplyUndoRedo;
import markerpen.textbase.ValidateExtracts;
import markerpen.textbase.ValidateSpanOperations;
import markerpen.textbase.ValidateText;

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

        TestSuite suite = new TestSuite("Complete markerpen unit test suite");

        suite.addTestSuite(ValidateSpanOperations.class);
        suite.addTestSuite(ValidateText.class);
        suite.addTestSuite(ValidateExtracts.class);
        suite.addTestSuite(ValidateApplyUndoRedo.class);
        suite.addTestSuite(ValidateStackToDocBookConversion.class);

        return suite;
    }
}
