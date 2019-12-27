package io.reflectoring.coderadar.adapter;

import io.reflectoring.coderadar.vcs.UnableToCloneRepositoryException;
import io.reflectoring.coderadar.vcs.adapter.CloneRepositoryAdapter;
import io.reflectoring.coderadar.vcs.port.driver.clone.CloneRepositoryCommand;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CloneRepositoryAdapterTest {

  @TempDir public File folder;

  @Test
  public void test() throws UnableToCloneRepositoryException {
    URL testRepoURL = this.getClass().getClassLoader().getResource("test-repository");

    CloneRepositoryAdapter cloneRepositoryAdapter = new CloneRepositoryAdapter();
    cloneRepositoryAdapter.cloneRepository(
        new CloneRepositoryCommand(testRepoURL.toString(), folder, "", ""));

    Assertions.assertEquals(3, folder.list().length);
    Assertions.assertEquals(".git", folder.list()[0]);
    Assertions.assertEquals("GetMetricsForCommitCommand.java", folder.list()[1]);
    Assertions.assertEquals("testModule1", folder.list()[2]);
  }

  @AfterEach
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(folder);
  }
}
