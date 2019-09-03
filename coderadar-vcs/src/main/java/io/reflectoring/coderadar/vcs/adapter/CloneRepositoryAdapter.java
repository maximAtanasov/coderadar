package io.reflectoring.coderadar.vcs.adapter;

import io.reflectoring.coderadar.vcs.UnableToCloneRepositoryException;
import io.reflectoring.coderadar.vcs.port.driven.CloneRepositoryPort;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.transport.GitProtocolConstants;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.springframework.stereotype.Service;

@Service
public class CloneRepositoryAdapter implements CloneRepositoryPort {

  @Override
  public void cloneRepository(String remoteUrl, File localDir)
      throws UnableToCloneRepositoryException {
    try {
      // TODO: support cloning with credentials for private repositories
      // TODO: support progress monitoring
        Git git = Git.init().setDirectory(localDir).call();
        StoredConfig config = git.getRepository().getConfig();
        config.setEnum(
                ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_EOL, CoreConfig.EOL.LF);
        config.save();
        git.fetch().setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*")).setRemote(remoteUrl).call();
        git.branchCreate().setForce(true).setName("master").setStartPoint("origin/master").call();
        git.checkout().setName("master").call();
      git.getRepository().close();
    } catch (GitAPIException | IOException e) {
      throw new UnableToCloneRepositoryException(e.getMessage());
    }
  }
}
