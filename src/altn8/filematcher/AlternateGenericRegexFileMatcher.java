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
import altn8.AlternateGenericFileExtensionRegexItem;
import altn8.AlternateGenericPrefixPostfixRegexItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class AlternateGenericRegexFileMatcher implements AlternateFileMatcher {
    private Pattern matchPattern;
    private String replacePattern;
    private String name;

    /**
     *
     */
    public AlternateGenericRegexFileMatcher(String currentFilename, AlternateConfiguration configuration) {
        // create regex and count groups
        GenRegex prefixGenRegex = createRegexPattern(configuration.getGenericPrefixRegexItems());
        GenRegex postfixGenRegex = createRegexPattern(configuration.getGenericPostfixRegexItems());

        // create full prefix/postfixPattern
        String prefixPattern = "^" + prefixGenRegex.pattern;
        String postfixPattern = postfixGenRegex.pattern + "(?:\\.(?:" + createFileExtensionPattern(configuration.getGenericFileExtensionRegexItems()) + "))?$";

        // get matcher capturing the name
        Matcher matcher = Pattern.compile(prefixPattern + "(\\w+?)" + postfixPattern).matcher(currentFilename);
        // we have a name if regex matches (prefixGroupCount + 1 to get the name-group (\w+?))
        name = matcher.matches() ? matcher.group(prefixGenRegex.groupCount + 1) : "";

        // we have our name so we can create our pattern to test filenames
        matchPattern = Pattern.compile(prefixPattern + Pattern.quote(name) + postfixPattern);

        // create replacePattern to build baseFilename ($1$2$3FooBar$4$5)
        StringBuilder sb = new StringBuilder();
        for (int i = 1, n = prefixGenRegex.groupCount; i <= n; i++) {
            sb.append('$').append(i);
        }
        sb.append(name);
        for (int i = prefixGenRegex.groupCount + 1, n = prefixGenRegex.groupCount + postfixGenRegex.groupCount; i <= n; i++) {
            sb.append('$').append(i);
        }
        replacePattern = sb.toString();

        /*
            ^
            (?:                                                    prefixPattern
                    (
                            [Tt]est_?
                    )?
                    (
                            I(?=[A-Z])
                    )?
                    (
                            Abstract(?=[A-Z])
                    )?
            )?                                                     /prefixPattern
            (\w+?)
            (?:                                                    postfixPattern
                    (
                            Impl
                    )?
                    (
                            [Tt]est
                    )?
                    (?:
                            (?:_\w{2}(?:_\w{2})?)?
                    )?
            )?                                                     /postfixPattern
            (?:\.(?:

            java|properties|html?|xml                              fileExtensions

            ))?
            $
        */
    }

    private static class GenRegex {
        int groupCount = 0;
        String pattern;
    }

    private GenRegex createRegexPattern(List<AlternateGenericPrefixPostfixRegexItem> items) {
        GenRegex result = new GenRegex();
        StringBuilder sb = new StringBuilder();
        for (AlternateGenericPrefixPostfixRegexItem item : items) {
            if (!item.hasError()) {
                if (sb.length() == 0) {
                    sb.append("(?:");
                }
                sb.append("(");
                if (item.isGrouping()) {
                    result.groupCount++;
                } else {
                    sb.append("?:");
                }
                sb.append(item.getExpression()).append(")?");
            }
        }
        if (sb.length() > 0) {
            sb.append(")?");
        }
        result.pattern = sb.toString();
        return result;
    }

    private String createFileExtensionPattern(List<AlternateGenericFileExtensionRegexItem> items) {
        StringBuilder sb = new StringBuilder();
        for (AlternateGenericFileExtensionRegexItem item : items) {
            if (!item.hasError()) {
                if (sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(item.getFileExtension());
            }
        }
        return sb.toString();
    }

    /**
     *
     */
    public boolean canProcess() {
        return name.length() > 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches(@NotNull String filename) {
        return matchPattern.matcher(filename).matches();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public String getBaseFilename(@NotNull String filename) {
        return matchPattern.matcher(filename).replaceAll(replacePattern);
    }
}
