package com.zolon.commit;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
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
    private JComboBox<ChangeType> changeType;

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
        for (ChangeType type : ChangeType.values()) {
            changeType.addItem(type);
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
        return (ChangeType) changeType.getSelectedItem();
    }

    private void restoreValuesFromParsedCommitMessage(CommitMessage commitMessage) {
        changeType.setSelectedItem(commitMessage.getType());
        scope.setSelectedItem(commitMessage.getScope());
        subject.setText(commitMessage.getSubject());
        details.setText(commitMessage.getDetails());
        broken.setText(commitMessage.getBroken());
        related.setText(commitMessage.getRelated());
    }
}
