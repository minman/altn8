package com.leapingfrogs.idea.plugins.alternate.action.ui;

import com.leapingfrogs.idea.plugins.alternate.action.AlternateConfiguration;

/**
 * To share data between UI and AlternateConfiguration
 */
interface DataInterface {
    /**
     * Pull data from {@link AlternateConfiguration}.
     *
     * @see #pushDataTo(AlternateConfiguration)
     */
    void pullDataFrom(AlternateConfiguration configuration);

    /**
     * Push data to {@link AlternateConfiguration}.
     *
     * @see #pullDataFrom(AlternateConfiguration)
     */
    public void pushDataTo(AlternateConfiguration configuration);

    /**
     * @return true if modified
     */
    public boolean isModified(AlternateConfiguration configuration);
}
