package altn8.ui;

import altn8.AlternateFreeRegexItem;
import altn8.AlternateUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @see #showDialog(String, AlternateFreeRegexItem , Runnable)
 */
final class AlternateFreeRegexItemDialog extends DialogWrapper {
    private JPanel topPanel;
    private JTextField matchExpressionTextField;
    private JTextField replaceExpressionTextField;

    /**
     * @param title
     * @param item
     */
    private AlternateFreeRegexItemDialog(@NotNull String title, @NotNull AlternateFreeRegexItem item) {
        super(true);
        setTitle("Free RegEx: " + title);
        init();
        // matchExpressionTextField
        matchExpressionTextField.setText(item.getMatchExpression());
        matchExpressionTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                matchExpressionTextField.select(0, matchExpressionTextField.getText().length());
            }
        });
        matchExpressionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                changed();
            }
        });
        // replaceExpressionTextField
        replaceExpressionTextField.setText(item.getReplaceExpression());
        replaceExpressionTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                replaceExpressionTextField.select(0, replaceExpressionTextField.getText().length());
            }
        });
        replaceExpressionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                changed();
            }
        });
        //
        changed();
    }

    private void changed() {
        String matchExpression = matchExpressionTextField.getText();
        String replaceExpression = replaceExpressionTextField.getText();
        // OK enabled if we have input...
        setOKActionEnabled(matchExpression.length() > 0 && replaceExpression.length() > 0);
        // Show errors (Error ist shown in html, so we convert our Messege to html)
        setErrorText(AlternateUtils.toHTML(AlternateFreeRegexItem.getErrorText(matchExpression, replaceExpression)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent createCenterPanel() {
        return topPanel;
    }

    /**
     * @return get current item from entries
     */
    @NotNull
    private AlternateFreeRegexItem getItem() {
        return new AlternateFreeRegexItem(matchExpressionTextField.getText(), replaceExpressionTextField.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getPreferredFocusedComponent() {
        return matchExpressionTextField;
    }

    /**
     * 
     */
    public static void showDialog(@NotNull String title, @NotNull AlternateFreeRegexItem currentItem, @NotNull AbstractDataPanel.Runnable<AlternateFreeRegexItem> runnable) {
        AlternateFreeRegexItemDialog dialog = new AlternateFreeRegexItemDialog(title, currentItem);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            AlternateFreeRegexItem newItem = dialog.getItem();
            if (!newItem.equals(currentItem) && !newItem.isEmpty()) {
                runnable.run(newItem);
            }
        }
    }
}
