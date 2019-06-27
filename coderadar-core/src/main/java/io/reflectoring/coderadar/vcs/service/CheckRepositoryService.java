package io.reflectoring.coderadar.vcs.service;

import io.reflectoring.coderadar.vcs.port.driven.CheckRepositoryPort;
import io.reflectoring.coderadar.vcs.port.driver.CheckRepositoryUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class CheckRepositoryService implements CheckRepositoryUseCase {

  private final CheckRepositoryPort checkRepositoryPort;

  @Autowired
  public CheckRepositoryService(CheckRepositoryPort checkRepositoryPort) {
    this.checkRepositoryPort = checkRepositoryPort;
  }

  @Override
  public boolean isRepository(Path folder) {
    return checkRepositoryPort.isRepository(folder);
  }
}