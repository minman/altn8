package altn8;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public abstract class AbstractRegexItem {
    @Nullable
    protected String errorText;

    /**
     *
     */
    protected abstract void updateErrorText();

    /**
     *
     */
    @Nullable
    public String getErrorText() {
        return errorText;
    }

    /**
     *
     */
    public boolean hasError() {
        return errorText != null;
    }
}
