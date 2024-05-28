package com.zolon.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Base from <a href="https://github.com/MobileTribe/commit-template-idea-plugin">MobileTribe/commit-template-idea-plugin</a>
 *
 * @author Damien Arrachequesne
 * @author manoo
 */
public class CommitDialog extends DialogWrapper {

    private final CommitPanel panel;

    CommitDialog(@Nullable Project project, CommitMessage commitMessage) {
        super(project);
        assert project != null;
        panel = new CommitPanel(project, commitMessage);
        setTitle("Commit");
        setOKButtonText("OK");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel.getMainPanel();
    }

    CommitMessage getCommitMessage() {
        return panel.getCommitMessage();
    }

}
