package com.leapingfrogs.idea.plugins.alternate.action.filematcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
abstract class AlternateFileMatcherTest {
    /**
     * Go thru {@link #getFilenames()} and return all matches
     * @return Array
     */
    protected String[] getMatchList(AlternateFileMatcher fileMatcher) {
        List<String> list = new ArrayList<String>();
        for (String filename : getFilenames()) {
            if (fileMatcher.matches(filename)) {
                list.add(fileMatcher.getBaseFilename(filename) + "|" + filename);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected abstract String[] getFilenames();
}
