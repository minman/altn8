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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * data item of a free regex (immutable).
 */
public final class AlternateFreeRegexItem extends AbstractRegexItem {
    @NotNull
    public String matchExpression;
    @NotNull
    public String replaceExpression;

    /**
     *
     */
    public static AlternateFreeRegexItem of(@NotNull String matchExpression, @NotNull String replaceExpression) {
        AlternateFreeRegexItem item = new AlternateFreeRegexItem();
        item.matchExpression = matchExpression;
        item.replaceExpression = replaceExpression;
        return item;
    }

    /**
     * @return
     */
    @Override
    protected String validate() {
        return validate(matchExpression, replaceExpression);
    }

    /**
     * @return  true if emtpy
     */
    public boolean isEmpty() {
        return matchExpression.length() == 0 && matchExpression.length() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlternateFreeRegexItem that = (AlternateFreeRegexItem) o;

        return matchExpression.equals(that.matchExpression) && replaceExpression.equals(that.replaceExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = matchExpression.hashCode();
        result = 31 * result + replaceExpression.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AlternateFreeRegexItem{" +
                "matchExpression='" + matchExpression + '\'' +
                ", replaceExpression='" + replaceExpression + '\'' +
                '}';
    }

    /**
     * @param matchExpression
     * @param replaceExpression
     * @return ErrorText or null if ok
     */
    @Nullable
    public static String validate(@NotNull String matchExpression, @NotNull String replaceExpression) {
        // matchExpression
        Pattern matchPattern = null;
        try {
            // only test if we have input
            if (matchExpression.length() > 0) {
                matchPattern = Pattern.compile(matchExpression);
            }
        } catch (PatternSyntaxException e) {
            return "Match Expression: " +  e.getMessage();
        }
        // replaceExpression
        if (replaceExpression.length() > 0) {
            int groupCount = matchPattern == null ? 0 : matchPattern.matcher("dummy").groupCount(); // weird but we need a Matcher to access groupCount from Pattern...
            int highestIndex = getHighestIndex(replaceExpression, groupCount);
            //
            if (highestIndex < 0) {
                return "Replace Expression: Illegal group reference"; // same message like matcher
            }
            if (highestIndex > groupCount) {
                return "Replace Expression: No group " + highestIndex;
            }
            // ...all other cases are legal...
        }

        return null;
    }

    /**
     * derived from {@link java.util.regex.Matcher#appendReplacement}
     */
    private static int getHighestIndex(String replacement, int groupCount) throws IllegalArgumentException {
        int highestIndex = 0;
        // Process substitution string to replace group references with groups
        int cursor = 0;

        while (cursor < replacement.length()) {
            char nextChar = replacement.charAt(cursor);
            cursor++;
            if (nextChar == '\\') {
                cursor++;
            } else if (nextChar == '$') {
                if (cursor >= replacement.length()) {
                    return -1;
                }
                // The first number is always a group
                int refNum = (int) replacement.charAt(cursor) - '0';
                if ((refNum < 0) || (refNum > 9)) {
                    return -1; // throw new IllegalArgumentException("Illegal group reference");
                }
                cursor++;

                // Capture the largest legal group string
                boolean done = false;
                while (!done) {
                    if (cursor >= replacement.length()) {
                        break;
                    }
                    int nextDigit = replacement.charAt(cursor) - '0';
                    if ((nextDigit < 0) || (nextDigit > 9)) { // not a number
                        break;
                    }
                    int newRefNum = (refNum * 10) + nextDigit;
                    if (groupCount < newRefNum) {
                        done = true;
                    } else {
                        refNum = newRefNum;
                        cursor++;
                    }
                }

                // Append group
                if (refNum > highestIndex) {
                    highestIndex = refNum;
                    if (highestIndex > groupCount) {
                        return highestIndex;
                    }
                }
            }
        }
        return highestIndex;
    }
}
