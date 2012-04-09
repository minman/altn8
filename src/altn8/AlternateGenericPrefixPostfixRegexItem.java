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
 * data item of a generic regex (immutable).
 */
public final class AlternateGenericPrefixPostfixRegexItem extends AbstractRegexItem {
    @NotNull
    public GenericType type;
    @NotNull
    public String expression;
    public boolean grouping;
    @NotNull
    public String description;

    /**
     *
     */
    public static AlternateGenericPrefixPostfixRegexItem of(@NotNull GenericType type, @NotNull String expression, boolean grouping, @NotNull String description) {
        AlternateGenericPrefixPostfixRegexItem item = new AlternateGenericPrefixPostfixRegexItem();
        item.type = type;
        item.expression = expression;
        item.grouping = grouping;
        item.description = description;
        return item;
    }

    /**
     * @return
     */
    @Override
    protected String validate() {
        return validate(expression);
    }

    /**
     * @return  true if emtpy
     */
    public boolean isEmpty() {
        return expression.length() == 0; // type is irrelevant
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

        AlternateGenericPrefixPostfixRegexItem that = (AlternateGenericPrefixPostfixRegexItem) o;

        return grouping == that.grouping && type == that.type && expression.equals(that.expression) && description.equals(that.description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + expression.hashCode();
        result = 31 * result + (grouping ? 1 : 0);
        result = 31 * result + description.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AlternateGenericPrefixPostfixRegexItem{" +
                "type=" + type +
                ", expression='" + expression + '\'' +
                ", grouping=" + grouping +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * @param expression
     * @return ErrorText or null if ok
     */
    @Nullable
    public static String validate(@NotNull String expression) {
        // expression
        Pattern matchPattern = null;
        try {
            // only test if we have input
            if (expression.length() > 0) {
                matchPattern = Pattern.compile(expression);
            }
        } catch (PatternSyntaxException e) {
            return "Match Expression: " +  e.getMessage();
        }
        // groupCount
        if (matchPattern != null && matchPattern.matcher("dummy").groupCount() > 0) { // weird but we need a Matcher to access groupCount from Pattern...
            return "Capturing groups are not allowed here.\nPlease replace '(' with non-capturing group '(?:'"; // capturing group 
        }
        // ...all other cases are legal...

        return null;
    }

    /**
     * The generic Type
     */
    public static enum GenericType {
        PREFIX("Prefix"),
        POSTFIX("Postfix");
        private String text;

        GenericType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
