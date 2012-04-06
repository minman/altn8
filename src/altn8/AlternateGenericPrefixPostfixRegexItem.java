package altn8;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * data item of a generic regex (immutable).
 */
public final class AlternateGenericPrefixPostfixRegexItem extends AbstractRegexItem {
    @NotNull
    private final GenericType type;
    @NotNull
    private final String expression;
    private final boolean grouping;

    private static final String XMLELEMENT_GENERICPREFIXPOSTFIXREGEXITEM = "genericPrefixPostfixItem";
    private static final String XMLATTRIBUTE_TYPE = "type";
    private static final String XMLATTRIBUTE_EXPRESSION = "expression";
    private static final String XMLATTRIBUTE_GROUPING = "grouping";

    /**
     *
     */
    public AlternateGenericPrefixPostfixRegexItem(@NotNull GenericType type, @NotNull String expression, boolean grouping) {
        this.type = type;
        this.expression = expression;
        this.grouping = grouping;
        updateErrorText();
    }

    /**
     *
     */
    public AlternateGenericPrefixPostfixRegexItem(@NotNull Element element) {
        GenericType type = null;
        try {
            String typeName = element.getAttributeValue(XMLATTRIBUTE_TYPE);
            if (typeName != null && typeName.length() > 0) {
                type = GenericType.valueOf(typeName);
            }
        } catch (IllegalArgumentException e) {
            // enum not found... go on with default...
        }
        this.type = type == null ? GenericType.POSTFIX : type;
        this.expression = element.getAttributeValue(XMLATTRIBUTE_EXPRESSION, "");
        this.grouping = Boolean.parseBoolean(element.getAttributeValue(XMLATTRIBUTE_GROUPING, "true"));
        updateErrorText();
    }

    /**
     *
     */
    protected  void updateErrorText() {
        errorText = getErrorText(expression);
    }

    /**
     *
     */
    @NotNull
    public GenericType getType() {
        return type;
    }

    /**
     *
     */
    @NotNull
    public String getExpression() {
        return expression;
    }

    /**
     *
     */
    public boolean isGrouping() {
        return grouping;
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

        return grouping == that.grouping && type == that.type && expression.equals(that.expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + expression.hashCode();
        result = 31 * result + (grouping ? 1 : 0);
        return result;
    }

    /**
     * @return this item as JDOM-Element
     */
    @NotNull
    Element asJDomElement() {
        Element element = new Element(XMLELEMENT_GENERICPREFIXPOSTFIXREGEXITEM);
        element.setAttribute(XMLATTRIBUTE_TYPE, getType().name());
        element.setAttribute(XMLATTRIBUTE_EXPRESSION, getExpression());
        element.setAttribute(XMLATTRIBUTE_GROUPING, Boolean.toString(isGrouping()));
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AlternateGenericPrefixPostfixRegexItem{" + XMLATTRIBUTE_TYPE + "=" + type + ", " +
                XMLATTRIBUTE_EXPRESSION + "='" + expression + '\'' + ", " +
                XMLATTRIBUTE_GROUPING + "=" + Boolean.toString(grouping) + '}';
    }
    
    /**
     * @param expression
     * @return ErrorText or null if ok
     */
    @Nullable
    public static String getErrorText(@NotNull String expression) {
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
