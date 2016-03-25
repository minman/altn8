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
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

public class AlternateApplicationConfigurable implements Configurable {
    private AlternateConfigurationPanel dataInterface;

    private static final String DISPLAYNAME = "AltN8";

    public String getDisplayName() {
        return DISPLAYNAME;
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
        return dataInterface != null && dataInterface.isModified(AlternateConfiguration.getInstance());
    }

    public void apply() throws ConfigurationException {
        if (dataInterface != null) {
            dataInterface.pushDataTo(AlternateConfiguration.getInstance());
        }
    }

    public void reset() {
        if (dataInterface != null) {
            dataInterface.pullDataFrom(AlternateConfiguration.getInstance());
        }
    }

    public void disposeUIResources() {
        dataInterface = null;
    }
}
