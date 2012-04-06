package altn8;

import altn8.ui.AlternateConfigurationPanel;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

public class AlternateApplicationComponent extends AlternateConfiguration implements ApplicationComponent, Configurable {
    private AlternateConfigurationPanel dataInterface;

    private static final String COMPONENTNAME = "AlternateApplicationComponent";
    private static final String DISPLAYNAME = "AltN8";

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public String getComponentName() {
        return COMPONENTNAME;
    }

    public String getDisplayName() {
        return DISPLAYNAME;
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (dataInterface == null) {
            dataInterface = new AlternateConfigurationPanel();
        }
        return dataInterface;
    }

    public boolean isModified() {
        return dataInterface != null && dataInterface.isModified(this);
    }

    public void apply() throws ConfigurationException {
        if (dataInterface != null) {
            dataInterface.pushDataTo(this);
        }
    }

    public void reset() {
        if (dataInterface != null) {
            dataInterface.pullDataFrom(this);
        }
    }

    public void disposeUIResources() {
        dataInterface = null;
    }
}
