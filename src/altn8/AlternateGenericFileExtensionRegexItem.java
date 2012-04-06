package altn8;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * data item of a generic regex (immutable).
 */
public final class AlternateGenericFileExtensionRegexItem extends AbstractRegexItem {
    @NotNull
    private final String fileExtension;

    private static final String XMLELEMENT_GENERICFILEEXTENSIONREGEXITEM = "genericFileExtensionItem";
    private static final String XMLATTRIBUTE_FILEEXTENSION = "fileExtension";

    /**
     *
     */
    public AlternateGenericFileExtensionRegexItem(@NotNull String fileExtensionRegex) {
        this.fileExtension = fileExtensionRegex;
        updateErrorText();
    }

    /**
     *
     */
    public AlternateGenericFileExtensionRegexItem(@NotNull Element element) {
        this.fileExtension = element.getAttributeValue(XMLATTRIBUTE_FILEEXTENSION, "");
        updateErrorText();
    }

    /**
     *
     */
    protected void updateErrorText() {
        errorText = getErrorText(fileExtension);
    }

    /**
     *
     */
    @NotNull
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * @return  true if emtpy
     */
    public boolean isEmpty() {
        return fileExtension.length() == 0;
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

        AlternateGenericFileExtensionRegexItem that = (AlternateGenericFileExtensionRegexItem) o;

        return fileExtension.equals(that.fileExtension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return fileExtension.hashCode();
    }

    /**
     * @return this item as JDOM-Element
     */
    @NotNull
    Element asJDomElement() {
        Element element = new Element(XMLELEMENT_GENERICFILEEXTENSIONREGEXITEM);
        element.setAttribute(XMLATTRIBUTE_FILEEXTENSION, getFileExtension());
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AlternateGenericFileExtensionRegexItem{" + XMLATTRIBUTE_FILEEXTENSION + "='" + fileExtension + "'}";
    }
    
    /**
     * @param expression
     * @return ErrorText or null if ok
     */
    @Nullable
    public static String getErrorText(@NotNull String expression) {
        // fileExtension
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
}
