package altn8.ui;

import altn8.AlternateConfiguration;

/**
 * To share data between UI and AlternateConfiguration
 */
interface DataInterface {
    /**
     * Pull data from {@link altn8.AlternateConfiguration}.
     *
     * @see #pushDataTo(altn8.AlternateConfiguration)
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
