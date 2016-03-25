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
import altn8.AlternateGenericPrefixPostfixRegexItem;
import com.intellij.openapi.fileTypes.*;
import com.intellij.util.PatternUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class AlternateGenericRegexFileMatcher implements AlternateFileMatcher {
    private Pattern matchPattern;
    private int matchPatternGroupCount;
    private String name;

    /**
     *
     */
    public AlternateGenericRegexFileMatcher(String currentFilename, AlternateConfiguration configuration) {
        // create regex and count groups
        GenRegex prefixGenRegex = createRegexPattern(configuration.genericPrefixRegexItems);
        GenRegex postfixGenRegex = createRegexPattern(configuration.genericPostfixRegexItems);

        // create full prefix/postfixPattern
        String prefixPattern = "^" + prefixGenRegex.pattern;
        String postfixPattern = postfixGenRegex.pattern + "(?:\\.(?:" + createFileExtensionPattern() + "))?$";

        // get matcher capturing the name
        Matcher matcher = Pattern.compile(prefixPattern + "(\\w+?)" + postfixPattern).matcher(currentFilename);
        // we have a name if regex matches (prefixGroupCount + 1 to get the name-group (\w+?))
        name = matcher.matches() ? matcher.group(prefixGenRegex.groupCount + 1) : "";

        // we have our name so we can create our pattern to test filenames
        StringBuilder sb = new StringBuilder();
        sb.append(prefixPattern);
        if (configuration.caseInsensitiveBasename) {
            sb.append("(?i)");
        }
        sb.append('(').append(Pattern.quote(name)).append(')');
        if (configuration.caseInsensitiveBasename) {
            sb.append("(?-i)");
        }
        sb.append(postfixPattern);
        matchPattern = Pattern.compile(sb.toString());

        //
        matchPatternGroupCount = prefixGenRegex.groupCount + postfixGenRegex.groupCount + 1; // + 1 for the name

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
                if (item.grouping) {
                    result.groupCount++;
                } else {
                    sb.append("?:");
                }
                sb.append(item.expression).append(")?");
            }
        }
        if (sb.length() > 0) {
            sb.append(")?");
        }
        result.pattern = sb.toString();
        return result;
    }

    private String createFileExtensionPattern() {
        StringBuilder sb = new StringBuilder();
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        for (FileType fileType : fileTypeManager.getRegisteredFileTypes()) {
            for (FileNameMatcher fileNameMatcher : fileTypeManager.getAssociations(fileType)) {
                String extension = null;
                if (fileNameMatcher instanceof ExtensionFileNameMatcher) {
                    extension = ((ExtensionFileNameMatcher) fileNameMatcher).getExtension();
                } else if (fileNameMatcher instanceof WildcardFileNameMatcher) {
                    String pattern = ((WildcardFileNameMatcher) fileNameMatcher).getPattern();
                    if (pattern.startsWith("*.")) { // we only support matcher starting with *. assuming it's a file extension
                        extension = PatternUtil.convertToRegex(pattern.substring(2));
                    }
                }
                if (extension != null) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(extension);
                }
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
        return matchPattern.matcher(filename).replaceAll(getReplacePattern(matchPatternGroupCount));
    }

    private static final Map<Integer, String> REPLACEPATTERNS = new HashMap<Integer, String>();

    /**
     * @return replacePattern to build baseFilename (ex: groupCount is 5 -> "$1$2$3$4$5")
     */
    private static String getReplacePattern(int groupCount) {
        String replacePattern = REPLACEPATTERNS.get(groupCount);
        if (replacePattern == null) {
            StringBuilder sb = new StringBuilder(groupCount * 2);
            for (int i = 0; i < groupCount; i++) {
                sb.append('$').append(i + 1);
            }
            replacePattern = sb.toString();
            REPLACEPATTERNS.put(groupCount, replacePattern);
        }
        return replacePattern;
    }
}
