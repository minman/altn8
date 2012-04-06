package altn8;

import altn8.filematcher.AlternateFileMatcher;
import altn8.filematcher.AlternateFreeRegexFileMatcher;
import altn8.filematcher.AlternateGenericRegexFileMatcher;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateConfiguration implements JDOMExternalizable {
    // freeRegex
    private boolean freeRegexActive;
    private List<AlternateFreeRegexItem> freeRegexItems;
    // genericRegex
    private boolean genericRegexActive;
    private List<AlternateGenericPrefixPostfixRegexItem> genericPrefixRegexItems;
    private List<AlternateGenericPrefixPostfixRegexItem> genericPostfixRegexItems;
    private List<AlternateGenericFileExtensionRegexItem> genericFileExtensionRegexItems;

    private static final String XMLATTRIBUTE_ACTIVE = "active";

    private static final String XMLELEMENT_FREEREGEXMAPPINGS = "alternateMappings"; // 'mapping' is the old name for FreeRegexItem -> name should stay for compatibility

    private static final String XMLELEMENT_GENERICREGEXMAPPINGS = "genericMappings";
    private static final String XMLELEMENT_GENERICPREFIXPOSTFIXREGEXITEMS = "genericPrefixPostfixItems";
    private static final String XMLELEMENT_GENERICFILEEXTENSIONREGEXITEMS = "genericFileExtensionItems";

    public AlternateConfiguration() {
        // genericRegex
        genericRegexActive = true;
        genericPrefixRegexItems = new ArrayList<AlternateGenericPrefixPostfixRegexItem>();
        genericPostfixRegexItems = new ArrayList<AlternateGenericPrefixPostfixRegexItem>();
        genericFileExtensionRegexItems = new ArrayList<AlternateGenericFileExtensionRegexItem>();
        // -> fill defaults
        addDefaultGenericPrefixRegexItems();
        addDefaultGenericPostfixRegexItems();
        addDefaultGenericFileExtensionItems();
        // freeRegex
        freeRegexActive = false; // default false, genericRegex are forced
        freeRegexItems = new ArrayList<AlternateFreeRegexItem>();
        // -> fill defaults
        addDefaultFreeRegexItems();
    }

    /**
     * @return
     */
    public boolean isFreeRegexActive() {
        return freeRegexActive;
    }

    /**
     * @return
     */
    public void setFreeRegexActive(boolean freeRegexActive) {
        this.freeRegexActive = freeRegexActive;
    }

    /**
     * @return
     */
    @NotNull
    public List<AlternateFreeRegexItem> getFreeRegexItems() {
        return freeRegexItems;
    }

    /**
     * @return
     */
    public boolean isGenericRegexActive() {
        return genericRegexActive;
    }

    /**
     * @return
     */
    public void setGenericRegexActive(boolean genericRegexActive) {
        this.genericRegexActive = genericRegexActive;
    }

    /**
     * @return
     */
    @NotNull
    public List<AlternateGenericPrefixPostfixRegexItem> getGenericPostfixRegexItems() {
        return genericPostfixRegexItems;
    }

    /**
     * @return
     */
    @NotNull
    public List<AlternateGenericPrefixPostfixRegexItem> getGenericPrefixRegexItems() {
        return genericPrefixRegexItems;
    }

    /**
     * @return
     */
    public List<AlternateGenericFileExtensionRegexItem> getGenericFileExtensionRegexItems() {
        return genericFileExtensionRegexItems;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void readExternal(Element rootElement) throws InvalidDataException {
        { // freeRegex
            Element freeRegexRootElement = rootElement.getChild(XMLELEMENT_FREEREGEXMAPPINGS);

            if (freeRegexRootElement != null) {
                freeRegexItems.clear();
                freeRegexActive = Boolean.valueOf(freeRegexRootElement.getAttributeValue(XMLATTRIBUTE_ACTIVE, "true"));

                List<Element> elements = (List<Element>) freeRegexRootElement.getChildren();
                for (Element element : elements) {
                    AlternateFreeRegexItem item = new AlternateFreeRegexItem(element);
                    if (!item.isEmpty()) {
                        freeRegexItems.add(item);
                    }
                }
            }
        }

        { // genericRegex
            Element genericRegexRootElement = rootElement.getChild(XMLELEMENT_GENERICREGEXMAPPINGS);

            if (genericRegexRootElement != null) {
                genericRegexActive = Boolean.valueOf(genericRegexRootElement.getAttributeValue(XMLATTRIBUTE_ACTIVE, "true"));

                // genericPrefixPostfix
                Element genericPrefixPostfixRootElement = genericRegexRootElement.getChild(XMLELEMENT_GENERICPREFIXPOSTFIXREGEXITEMS);
                if (genericPrefixPostfixRootElement != null) {
                    genericPrefixRegexItems.clear();
                    genericPostfixRegexItems.clear();
                    List<Element> elements = (List<Element>) genericPrefixPostfixRootElement.getChildren();
                    for (Element element : elements) {
                        AlternateGenericPrefixPostfixRegexItem item = new AlternateGenericPrefixPostfixRegexItem(element);
                        if (!item.isEmpty()) {
                            switch (item.getType()) {
                                case PREFIX:
                                    genericPrefixRegexItems.add(item);
                                    break;
                                case POSTFIX:
                                    genericPostfixRegexItems.add(item);
                                    break;
                                default:
                                    throw new IllegalArgumentException("unknown generic type " + item.getType());
                            }
                        }
                    }
                }

                // genericFileExtensions
                Element genericFileExtensionRootElement = genericRegexRootElement.getChild(XMLELEMENT_GENERICFILEEXTENSIONREGEXITEMS);
                if (genericFileExtensionRootElement != null) {
                    genericFileExtensionRegexItems.clear();
                    List<Element> elements = (List<Element>) genericFileExtensionRootElement.getChildren();
                    for (Element element : elements) {
                        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem(element));
                    }
                }
            }
        }
    }

    /**
     *
     */
    private void addDefaultFreeRegexItems() {
        freeRegexItems.add(new AlternateFreeRegexItem("^Test(.*?)\\.java$", "$1.java"));
        freeRegexItems.add(new AlternateFreeRegexItem("^(.*?)\\.java$", "Test$1.java"));
        freeRegexItems.add(new AlternateFreeRegexItem("^(.*?)Test\\.java$", "$1.java"));
        freeRegexItems.add(new AlternateFreeRegexItem("^(.*?)\\.java$", "$1Test.java"));
    }

    /**
     *
     */
    private void addDefaultGenericPrefixRegexItems() {
        genericPrefixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "[Tt]est_?", true));
        genericPrefixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "I(?=[A-Z])", true));
        genericPrefixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX, "Abstract(?=[A-Z])", true));
    }

    /**
     *
     */
    private void addDefaultGenericPostfixRegexItems() {
        genericPostfixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "Impl", true));
        genericPostfixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "[Tt]est", true));
        genericPostfixRegexItems.add(new AlternateGenericPrefixPostfixRegexItem(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX, "(?:_\\w{2}(?:_\\w{2})?)", false));
    }

    /**
     *
     */
    private void addDefaultGenericFileExtensionItems() {
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("java"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("properties"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("html?"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("sql"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("xml"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("dtd"));
        genericFileExtensionRegexItems.add(new AlternateGenericFileExtensionRegexItem("xsd"));
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(Element rootElement) throws WriteExternalException {
        { // freeRegex
            Element freeRegexRootElement = new Element(XMLELEMENT_FREEREGEXMAPPINGS);
            rootElement.addContent(freeRegexRootElement);

            freeRegexRootElement.setAttribute(XMLATTRIBUTE_ACTIVE, Boolean.toString(freeRegexActive));

            for (AlternateFreeRegexItem item : freeRegexItems) {
                freeRegexRootElement.addContent(item.asJDomElement());
            }
        }
        { // genericRegex
            Element genericRegexRootElement = new Element(XMLELEMENT_GENERICREGEXMAPPINGS);
            rootElement.addContent(genericRegexRootElement);

            genericRegexRootElement.setAttribute(XMLATTRIBUTE_ACTIVE, Boolean.toString(genericRegexActive));

            // genericPrefixPostfix
            Element genericPrefixPostfixRootElement = new Element(XMLELEMENT_GENERICPREFIXPOSTFIXREGEXITEMS);
            genericRegexRootElement.addContent(genericPrefixPostfixRootElement);
            for (AlternateGenericPrefixPostfixRegexItem item : genericPrefixRegexItems) {
                genericPrefixPostfixRootElement.addContent(item.asJDomElement());
            }
            for (AlternateGenericPrefixPostfixRegexItem item : genericPostfixRegexItems) {
                genericPrefixPostfixRootElement.addContent(item.asJDomElement());
            }

            // genericFileExtensions
            Element genericFileExtensionRootElement = new Element(XMLELEMENT_GENERICFILEEXTENSIONREGEXITEMS);
            genericRegexRootElement.addContent(genericFileExtensionRootElement);
            for (AlternateGenericFileExtensionRegexItem item : genericFileExtensionRegexItems) {
                genericFileExtensionRootElement.addContent(item.asJDomElement());
            }
        }
    }

    /**
     * @return  List with currently active FileMatchers ()
     */
    public List<AlternateFileMatcher> getFileMatchers(String currentFilename) {
        List<AlternateFileMatcher> result = new ArrayList<AlternateFileMatcher>();
        // genericRegexActive (before freeRegexItems, because generic groups)
        if (genericRegexActive) {
            AlternateGenericRegexFileMatcher fileMatcher = new AlternateGenericRegexFileMatcher(currentFilename, this);
            if (fileMatcher.canProcess()) {
                result.add(fileMatcher);
            }
        }
        // freeRegexItems
        if (freeRegexActive) {
            AlternateFreeRegexFileMatcher fileMatcher = new AlternateFreeRegexFileMatcher(currentFilename, this);
            if (fileMatcher.canProcess()) {
                result.add(fileMatcher);
            }
        }
        return result;
    }
}
