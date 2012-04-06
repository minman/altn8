package com.leapingfrogs.idea.plugins.alternate.action.filematcher;

import com.leapingfrogs.idea.plugins.alternate.action.AlternateConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class AlternateGenericRegexFileMatcherTest extends AlternateFileMatcherTest {
    /**
     * Our filelist with all files in project to test
     */
    private static final String[] FILENAMES = new String[] {
            "FooBar.java",
            "FooBar.properties",
            "FooBar_en.properties",
            "FooBar_en_UK.properties",

            "FooBarTest.java",
            "FooBarTest.properties",
            "FooBarTest_en.properties",
            "FooBarTest_en_UK.properties",

            "AbstractFooBar.java",
            "AbstractFooBar.properties",
            "AbstractFooBar_en.properties",
            "AbstractFooBar_en_UK.properties",

            "AbstractFooBarTest.java",
            "AbstractFooBarTest.properties",
            "AbstractFooBarTest_en.properties",
            "AbstractFooBarTest_en_UK.properties",

            "MyClass.java",
            "MyClass.properties",
            "MyClass_en.properties",
            "MyClass_en_UK.properties",

            "MyClassTest.java",
            "MyClassTest.properties",
            "MyClassTest_en.properties",
            "MyClassTest_en_UK.properties",

            "AbstractMyClass.java",
            "AbstractMyClass.properties",
            "AbstractMyClass_en.properties",
            "AbstractMyClass_en_UK.properties",

            "AbstractMyClassTest.java",
            "AbstractMyClassTest.properties",
            "AbstractMyClassTest_en.properties",
            "AbstractMyClassTest_en_UK.properties",
    };

    @Override
    protected String[] getFilenames() {
        return FILENAMES;
    }

    @Test
    public void testMatches() throws Exception {
        // prepare test
        AlternateConfiguration configuration = new AlternateConfiguration();
        configuration.setFreeRegexActive(false);
        configuration.setGenericRegexActive(true);

        // make our tests

        assertArrayEquals(new Object[]{
                "MyClass|MyClass.java",
                "MyClass|MyClass.properties",
                "MyClass|MyClass_en.properties",
                "MyClass|MyClass_en_UK.properties",

                "MyClassTest|MyClassTest.java",
                "MyClassTest|MyClassTest.properties",
                "MyClassTest|MyClassTest_en.properties",
                "MyClassTest|MyClassTest_en_UK.properties",

                "AbstractMyClass|AbstractMyClass.java",
                "AbstractMyClass|AbstractMyClass.properties",
                "AbstractMyClass|AbstractMyClass_en.properties",
                "AbstractMyClass|AbstractMyClass_en_UK.properties",

                "AbstractMyClassTest|AbstractMyClassTest.java",
                "AbstractMyClassTest|AbstractMyClassTest.properties",
                "AbstractMyClassTest|AbstractMyClassTest_en.properties",
                "AbstractMyClassTest|AbstractMyClassTest_en_UK.properties",
        }, getMatchList(new AlternateGenericRegexFileMatcher("MyClass.java", configuration)));
    }
}
