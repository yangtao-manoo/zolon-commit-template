package com.zolon.commit;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Base from <a href="https://github.com/MobileTribe/commit-template-idea-plugin">MobileTribe/commit-template-idea-plugin</a>
 *
 * @author Damien Arrachequesne
 * @author manoo
 */
public class CommitPanel {
    private JPanel mainPanel;
    private JComboBox<String> scope;
    private JTextField subject;
    private JTextArea details;
    private JTextArea broken;
    private JTextField related;
    private JCheckBox wrapText;
    private JRadioButton featRadioButton;
    private JRadioButton fixRadioButton;
    private JRadioButton docsRadioButton;
    private JRadioButton styleRadioButton;
    private JRadioButton refactorRadioButton;
    private JRadioButton perfRadioButton;
    private JRadioButton testRadioButton;
    private JRadioButton buildRadioButton;
    private JRadioButton ciRadioButton;
    private JRadioButton choreRadioButton;
    private JRadioButton revertRadioButton;
    private ButtonGroup changeTypeGroup;

    CommitPanel(Project project, CommitMessage commitMessage) {
        File workingDirectory = new File(Objects.requireNonNull(project.getBasePath()));
        GitLogQuery.Result result = new GitLogQuery(workingDirectory).execute();
        if (result.isSuccess()) {
            scope.addItem(""); // no value by default
            result.getScopes().forEach(scope::addItem);
        }

        if (commitMessage != null) {
            restoreValuesFromParsedCommitMessage(commitMessage);
        }
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    CommitMessage getCommitMessage() {
        return new CommitMessage(
                getSelectedChangeType(),
                (String) scope.getSelectedItem(),
                subject.getText().trim(),
                details.getText().trim(),
                broken.getText().trim(),
                related.getText().trim(),
                wrapText.isSelected()
        );
    }

    private ChangeType getSelectedChangeType() {
        for (Enumeration<AbstractButton> buttons = changeTypeGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return ChangeType.valueOf(button.getActionCommand().toUpperCase());
            }
        }
        return null;
    }

    private void restoreValuesFromParsedCommitMessage(CommitMessage commitMessage) {
        if (commitMessage.getType() != null) {
            for (Enumeration<AbstractButton> buttons = changeTypeGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();

                if (button.getActionCommand().equalsIgnoreCase(commitMessage.getType().label())) {
                    button.setSelected(true);
                }
            }
        }
        scope.setSelectedItem(commitMessage.getScope());
        subject.setText(commitMessage.getSubject());
        details.setText(commitMessage.getDetails());
        broken.setText(commitMessage.getBroken());
        related.setText(commitMessage.getRelated());
    }
}
