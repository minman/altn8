package com.leapingfrogs.idea.plugins.alternate.action.filematcher;

import com.leapingfrogs.idea.plugins.alternate.action.AlternateConfiguration;
import com.leapingfrogs.idea.plugins.alternate.action.AlternateFreeRegexItem;
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
        for (AlternateFreeRegexItem item : configuration.getFreeRegexItems()) {
            if (!item.hasError()) {
                List<String> replaceItems = map.get(item.getMatchExpression());
                if (replaceItems == null) {
                    replaceItems = new ArrayList<String>();
                    map.put(item.getMatchExpression(), replaceItems);
                }
                replaceItems.add(item.getReplaceExpression());
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
