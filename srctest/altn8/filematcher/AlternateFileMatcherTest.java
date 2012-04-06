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
