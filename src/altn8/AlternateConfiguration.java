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
package altn8;

import com.intellij.util.xmlb.annotations.AbstractCollection;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateConfiguration {
    // general
    public boolean onlyFromModule;
    // freeRegex
    public boolean freeRegexActive;
    public List<AlternateFreeRegexItem> freeRegexItems;
    // genericRegex
    public boolean genericRegexActive;
    public boolean caseInsensitiveBasename;

    @AbstractCollection(surroundWithTag = false, elementTypes = AlternateGenericPrefixPostfixRegexItem.class)
    public List<AlternateGenericPrefixPostfixRegexItem> genericPrefixRegexItems;
    @AbstractCollection(surroundWithTag = false, elementTypes = AlternateGenericPrefixPostfixRegexItem.class)
    public List<AlternateGenericPrefixPostfixRegexItem> genericPostfixRegexItems;

    public AlternateConfiguration() {
        // genericRegex
        genericRegexActive = true;
        genericPrefixRegexItems = new ArrayList<AlternateGenericPrefixPostfixRegexItem>();
        genericPostfixRegexItems = new ArrayList<AlternateGenericPrefixPostfixRegexItem>();
        // -> fill defaults
        addDefaultGenericPrefixRegexItems();
        addDefaultGenericPostfixRegexItems();
        // freeRegex
        freeRegexActive = false; // default false, genericRegex are forced
        freeRegexItems = new ArrayList<AlternateFreeRegexItem>();
        // -> fill defaults
        addDefaultFreeRegexItems();
    }

    /**
     *
     */
    private void addDefaultFreeRegexItems() {
        freeRegexItems.add(AlternateFreeRegexItem.of("^Test(.*?)\\.java$", "$1.java"));
        freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)\\.java$", "Test$1.java"));
        freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)Test\\.java$", "$1.java"));
        freeRegexItems.add(AlternateFreeRegexItem.of("^(.*?)\\.java$", "$1Test.java"));
    }

    /**
     *
     */
    private void addDefaultGenericPrefixRegexItems() {
        genericPrefixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "[Tt]est_?", true, "Test classes and files"));
        genericPrefixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "I(?=[A-Z])", true, "Interfaces (ex: 'IEntity' but not 'Icon'"));
        genericPrefixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "Abstract(?=[A-Z])", true, "Abstract Classes"));
    }

    /**
     *
     */
    private void addDefaultGenericPostfixRegexItems() {
        genericPostfixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "Impl", true, "Implementations"));
        genericPostfixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "[Tt]est", true, "Test classes and files"));
        genericPostfixRegexItems.add(AlternateGenericPrefixPostfixRegexItem.of(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "(?:_\\w{2}(?:_\\w{2})?)", false, "Locales (ex: 'description_en_UK.properties')"));
    }
}
