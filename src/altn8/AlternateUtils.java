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

import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public final class AlternateUtils {
    private AlternateUtils() {
    }

    /**
     * @return Normal text to Html Text with font-family monospace. Needed for proportional output of RegEx-errors (have an ^ as problem marker)
     */
    public static String toHTML(@Nullable String text) {
        return text == null ? null : "<font face=\"monospace\">" + XmlStringUtil.escapeString(text, false).replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;") + "</font>";
    }
}
