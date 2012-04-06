package altn8.ui;

import altn8.AlternateConfiguration;
import com.intellij.openapi.ui.Splitter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateConfigurationPanel extends Splitter implements DataInterface {
    private List<DataInterface> dataInterfaces = new ArrayList<DataInterface>();

    public AlternateConfigurationPanel() {
        super(true, 0.5f);
        setHonorComponentsMinimumSize(true);
        // genericRegexPanel
        AlternateGenericRegexPanel genericRegexPanel = new AlternateGenericRegexPanel();
        dataInterfaces.add(genericRegexPanel);
        setFirstComponent(genericRegexPanel.getRootComponent());
        // freeRegexPanel
        AlternateFreeRegexPanel freeRegexPanel = new AlternateFreeRegexPanel();
        dataInterfaces.add(freeRegexPanel);
        setSecondComponent(freeRegexPanel.getRootComponent());
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
