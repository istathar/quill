/*
 * UnitTests.java (for the Quill application)
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
import quill.converter.ValidateFileNaming;
import quill.converter.ValidateTextChainToDocBookConversion;
import quill.converter.ValidateThereAndBackAgain;
import quill.textbase.ValidateApplyUndoRedo;
import quill.textbase.ValidateExtracts;
import quill.textbase.ValidateSpanOperations;
import quill.textbase.ValidateStructuralChange;
import quill.textbase.ValidateText;
import quill.textbase.ValidateUnicode;
import quill.textbase.ValidateWrapperExpansions;
import quill.ui.ValidateChangePropagation;

import com.operationaldynamics.junit.VerboseTestRunner;

public class UnitTests
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

        TestSuite suite = new TestSuite("Complete quill unit test suite");

        suite.addTestSuite(ValidateSpanOperations.class);
        suite.addTestSuite(ValidateText.class);
        suite.addTestSuite(ValidateUnicode.class);
        suite.addTestSuite(ValidateExtracts.class);
        suite.addTestSuite(ValidateApplyUndoRedo.class);
        suite.addTestSuite(ValidateTextChainToDocBookConversion.class);
        suite.addTestSuite(ValidateWrapperExpansions.class);
        suite.addTestSuite(ValidateStructuralChange.class);
        suite.addTestSuite(ValidateThereAndBackAgain.class);
        suite.addTestSuite(ValidateFileNaming.class);
        suite.addTestSuite(ValidateChangePropagation.class);

        return suite;
    }
}
