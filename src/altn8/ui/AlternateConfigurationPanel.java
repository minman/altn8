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
package altn8.ui;

import altn8.AlternateConfiguration;
import com.intellij.openapi.ui.Splitter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateConfigurationPanel implements DataInterface {
    private List<DataInterface> dataInterfaces = new ArrayList<DataInterface>();
    private JPanel regexSplitter;
    private JPanel rootComponent;
    private JCheckBox onlyFromModuleCheckBox;

    public AlternateConfigurationPanel() {
        dataInterfaces.add(new DataInterface() {
            public void pullDataFrom(AlternateConfiguration configuration) {
                onlyFromModuleCheckBox.setSelected(configuration.onlyFromModule);
            }

            public void pushDataTo(AlternateConfiguration configuration) {
                configuration.onlyFromModule = onlyFromModuleCheckBox.isSelected();
            }

            public boolean isModified(AlternateConfiguration configuration) {
               return onlyFromModuleCheckBox.isSelected() != configuration.onlyFromModule;
            }
        });

        ((Splitter) regexSplitter).setHonorComponentsMinimumSize(true);
        // genericRegexPanel
        AlternateGenericRegexPanel genericRegexPanel = new AlternateGenericRegexPanel();
        dataInterfaces.add(genericRegexPanel);
        ((Splitter) regexSplitter).setFirstComponent(genericRegexPanel.getRootComponent());
        // freeRegexPanel
        AlternateFreeRegexPanel freeRegexPanel = new AlternateFreeRegexPanel();
        dataInterfaces.add(freeRegexPanel);
        ((Splitter) regexSplitter).setSecondComponent(freeRegexPanel.getRootComponent());
    }

    private void createUIComponents() {
        regexSplitter = new Splitter(true, 0.5f);
    }

    public JPanel getRootComponent() {
        return rootComponent;
    }

    /**
     * {@inheritDoc}
     */
    public void pullDataFrom(AlternateConfiguration configuration) {
        for (DataInterface dataInterface : dataInterfaces) {
            dataInterface.pullDataFrom(configuration);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pushDataTo(AlternateConfiguration configuration) {
        for (DataInterface dataInterface : dataInterfaces) {
            dataInterface.pushDataTo(configuration);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModified(AlternateConfiguration configuration) {
        for (DataInterface dataInterface : dataInterfaces) {
            if (dataInterface.isModified(configuration)) {
                return true;
            }
        }
        return false;
    }
}
