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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class AlternateFreeRegexFileMatcher implements AlternateFileMatcher {
    private List<String> names;

    /**
     *
     */
    public AlternateFreeRegexFileMatcher(String currentFilename, AlternateConfiguration configuration) {
        // put all same matchExpressions in a map having a List with replaceExpressions
        Map<String, List<String>> map = new HashMap<String, List<String>>(); // <matchExpression, List<replaceExpression>>
        for (AlternateFreeRegexItem item : configuration.freeRegexItems) {
            if (!item.hasError()) {
                List<String> replaceItems = map.get(item.matchExpression);
                if (replaceItems == null) {
                    replaceItems = new ArrayList<String>();
                    map.put(item.matchExpression, replaceItems);
                }
                replaceItems.add(item.replaceExpression);
            }
        }
        // go thru all matchExpressions
        names = new ArrayList<String>(); // contains all possible filenames regarding to match/replaceExpression
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            // get a Matcher for currentFilename
            Matcher matcher = Pattern.compile(entry.getKey()).matcher(currentFilename);
            // if matches
            if (matcher.matches()) {
                // add all filenames by replacesPattern
                for (String replacePattern : entry.getValue()) {
                   names.add(matcher.replaceAll(replacePattern));
                }
            }
        }
    }


    /**
     *
     */
    public boolean canProcess() {
        return !names.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches(@NotNull String filename) {
        return names.contains(filename);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public String getBaseFilename(@NotNull String filename) {
        return ""; // Free Regex does not have any grouping...
    }
}
