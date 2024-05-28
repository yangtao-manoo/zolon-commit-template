package com.zolon.commit;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * Base from <a href="https://github.com/MobileTribe/commit-template-idea-plugin">MobileTribe/commit-template-idea-plugin</a>
 *
 * @author Damien Arrachequesne
 * @author manoo
 */
public class GitLogQueryTest {

    @Test
    @Ignore("manual testing")
    public void testExecute() {

        for (ChangeType value : ChangeType.values()) {
            System.out.println(value);
        }

        GitLogQuery.Result result = new GitLogQuery(new File("<absolute path>")).execute();

        System.out.println(result.isSuccess());
        System.out.println(result.getScopes());
    }

}