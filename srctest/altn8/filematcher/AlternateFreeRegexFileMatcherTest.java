package altn8.filematcher;

import altn8.AlternateConfiguration;
import altn8.AlternateFreeRegexItem;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 */
public class AlternateFreeRegexFileMatcherTest extends AlternateFileMatcherTest {
    /**
     * Our filelist with all files in project to test
     */
    private static final String[] FILENAMES = new String[] {
            "FooBar.java",
            "FooBar.properties",
            "FooBarTest.java",
            "FooBarTest.properties",

            "MyClass.java",
            "MyClass.properties",
            "MyClassTest.java",
            "MyClassTest.properties",
    };

    @Override
    protected String[] getFilenames() {
        return FILENAMES;
    }

    @Test
    public void testMatches() throws Exception {
        // prepare test
        AlternateConfiguration configuration = new AlternateConfiguration();
        configuration.setFreeRegexActive(true);
        configuration.setGenericRegexActive(false);
        configuration.getFreeRegexItems().clear();
        // java file finds Test and properties
        configuration.getFreeRegexItems().add(new AlternateFreeRegexItem("^(.*?)\\.java$", "$1Test.java"));
        configuration.getFreeRegexItems().add(new AlternateFreeRegexItem("^(.*?)\\.java$", "$1.properties"));
        // Test find only java
        configuration.getFreeRegexItems().add(new AlternateFreeRegexItem("^(.*?)Test\\.java", "$1.java"));
        // properties find only java
        configuration.getFreeRegexItems().add(new AlternateFreeRegexItem("^(.*?)\\.properties", "$1.java"));

        // make our tests

        assertArrayEquals(new Object[]{
                "|MyClass.properties",
                "|MyClassTest.java",
        }, getMatchList(new AlternateFreeRegexFileMatcher("MyClass.java", configuration)));

        assertArrayEquals(new Object[]{
                "|MyClass.java",
        }, getMatchList(new AlternateFreeRegexFileMatcher("MyClass.properties", configuration)));

        assertArrayEquals(new Object[]{
                "|MyClass.java",
                "|MyClassTest.properties",
        }, getMatchList(new AlternateFreeRegexFileMatcher("MyClassTest.java", configuration)));
    }
}
