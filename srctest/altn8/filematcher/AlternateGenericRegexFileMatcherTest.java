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
import com.intellij.lang.Language;
import com.intellij.mock.MockApplicationEx;
import com.intellij.mock.MockFileTypeManager;
import com.intellij.mock.MockLanguageFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 */
public class AlternateGenericRegexFileMatcherTest extends AlternateFileMatcherTest {

    private static final List FILE_NAME_MATCHERS = Arrays.asList(
            new ExtensionFileNameMatcher("java"),
            new ExtensionFileNameMatcher("properties"),
            new WildcardFileNameMatcher("*.htm?"),
            new ExtensionFileNameMatcher("xml")
    );

    @Before
    public void setUp() throws Exception {
        Disposable dummyDisposable = new Disposable() {public void dispose() {}};
        MockApplicationEx mockApplicationEx = new MockApplicationEx(dummyDisposable);
        ApplicationManager.setApplication(mockApplicationEx, dummyDisposable);
        mockApplicationEx.addComponent(FileTypeManager.class, new MockFileTypeManager(null) {
            @NotNull
            @Override
            public FileType[] getRegisteredFileTypes() {
                return new FileType[]{new MockLanguageFileType(Language.ANY, "foo")};
            }

            @NotNull
            @Override
            public List<FileNameMatcher> getAssociations(@NotNull FileType type) {
                //noinspection unchecked
                return FILE_NAME_MATCHERS;
            }
        });
    }

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

            // caseInsensitiveBasename
            "myclass.html",
            "myclass_de.html",
            "test_myclass.html",
            "test_myclass_de.html"
    };

    @Override
    protected String[] getFilenames() {
        return FILENAMES;
    }

    @Test
    public void testMatches() throws Exception {
        // prepare test
        AlternateConfiguration configuration = new AlternateConfiguration();
        configuration.freeRegexActive = false;
        configuration.genericRegexActive = true;

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

    @Test
    public void testMatchesCaseInsensitive() throws Exception {
        // prepare test
        AlternateConfiguration configuration = new AlternateConfiguration();
        configuration.freeRegexActive = false;
        configuration.genericRegexActive = true;
        configuration.caseInsensitiveBasename = true;

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

                "myclass|myclass.html",
                "myclass|myclass_de.html",
                "test_myclass|test_myclass.html",
                "test_myclass|test_myclass_de.html"

        }, getMatchList(new AlternateGenericRegexFileMatcher("myClass.xml", configuration)));
    }
}
