/*
 * Copyright 2012 The AltN8-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        configuration.freeRegexActive = true;
        configuration.genericRegexActive = false;
        configuration.freeRegexItems.clear();
        // java file finds Test and properties
        configuration.freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)\\.java$", "$1Test.java"));
        configuration.freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)\\.java$", "$1.properties"));
        // Test find only java
        configuration.freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)Test\\.java", "$1.java"));
        // properties find only java
        configuration.freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)\\.properties", "$1.java"));

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
