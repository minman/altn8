package altn8;

import com.intellij.xml.util.XmlStringUtil;

/**
 *
 */
public final class AlternateUtils {
    private AlternateUtils() {
    }

    /**
     * @return Normal text to Html Text with font-family monospace. Needed for proportional output of RegEx-errors (have an ^ as problem marker)
     */
    public static String toHTML(String text) {
        return text == null ? null : "<font face=\"monospace\">" + XmlStringUtil.escapeString(text, false).replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;") + "</font>";
    }
}
