import junit.framework.Test;
import junit.framework.TestSuite;
import textbase.ValidateApplyUndoRedo;
import textbase.ValidateChunks;
import textbase.ValidateText;

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

        TestSuite suite = new TestSuite("All Unit Tests for markerpen");

        suite.addTestSuite(ValidateChunks.class);
        suite.addTestSuite(ValidateText.class);
        suite.addTestSuite(ValidateApplyUndoRedo.class);

        return suite;
    }
}
