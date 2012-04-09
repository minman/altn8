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

import altn8.ui.AlternateConfigurationPanel;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;

import javax.swing.*;

@State(name = "AlternateApplicationComponent", storages = {@Storage(id = "altn8", file = "$APP_CONFIG$/altn8.xml")})
public class AlternateApplicationComponent implements ApplicationComponent, Configurable, PersistentStateComponent<AlternateConfiguration> {
    private AlternateConfigurationPanel dataInterface;
    private AlternateConfiguration alternateConfiguration = new AlternateConfiguration();

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
        return dataInterface.getRootComponent();
    }

    public boolean isModified() {
        return dataInterface != null && dataInterface.isModified(alternateConfiguration);
    }

    public void apply() throws ConfigurationException {
        if (dataInterface != null) {
            dataInterface.pushDataTo(alternateConfiguration);
        }
    }

    public void reset() {
        if (dataInterface != null) {
            dataInterface.pullDataFrom(alternateConfiguration);
        }
    }

    public void disposeUIResources() {
        dataInterface = null;
    }

    public AlternateConfiguration getState() {
        return alternateConfiguration;
    }

    public void loadState(AlternateConfiguration state) {
        XmlSerializerUtil.copyBean(state, alternateConfiguration);
    }
}
