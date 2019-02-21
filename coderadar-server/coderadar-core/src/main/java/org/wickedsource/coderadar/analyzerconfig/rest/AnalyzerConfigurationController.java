package org.wickedsource.coderadar.analyzerconfig.rest;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wickedsource.coderadar.analyzerconfig.domain.AnalyzerConfiguration;
import org.wickedsource.coderadar.analyzerconfig.domain.AnalyzerConfigurationRepository;
import org.wickedsource.coderadar.core.rest.validation.ResourceNotFoundException;
import org.wickedsource.coderadar.core.rest.validation.UserException;
import org.wickedsource.coderadar.project.domain.Project;
import org.wickedsource.coderadar.project.rest.ProjectVerifier;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/analyzers")
public class AnalyzerConfigurationController {

  private AnalyzerConfigurationRepository analyzerConfigurationRepository;

  private ProjectVerifier projectVerifier;

  private AnalyzerVerifier analyzerVerifier;

  @Autowired
  public AnalyzerConfigurationController(
      AnalyzerConfigurationRepository analyzerConfigurationRepository,
      ProjectVerifier projectVerifier,
      AnalyzerVerifier analyzerVerifier) {
    this.analyzerConfigurationRepository = analyzerConfigurationRepository;
    this.projectVerifier = projectVerifier;
    this.analyzerVerifier = analyzerVerifier;
  }

  @PostMapping
  public ResponseEntity<AnalyzerConfigurationResource> setAnalyzerConfiguration(
      @PathVariable Long projectId, @Valid @RequestBody AnalyzerConfigurationResource resource) {
    // TODO: overwrite, if existing
    analyzerVerifier.checkAnalyzerExistsOrThrowException(resource.getAnalyzerName());
    AnalyzerConfigurationResourceAssembler assembler =
        new AnalyzerConfigurationResourceAssembler(projectId);
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    if (analyzerConfigurationRepository.countByProjectIdAndAnalyzerName(
            projectId, resource.getAnalyzerName())
        > 0) {
      throw new UserException(
          String.format(
              "AnalyzerConfiguration for analyzer %s is already configured for this project!",
              resource.getAnalyzerName()));
    }
    AnalyzerConfiguration entity = assembler.toEntity(resource, project);
    AnalyzerConfiguration savedEntity = analyzerConfigurationRepository.save(entity);
    return new ResponseEntity<>(assembler.toResource(savedEntity), HttpStatus.CREATED);
  }

  @DeleteMapping(path = "/{analyzerConfigurationId}")
  public ResponseEntity<String> deleteAnalyzerConfigurationFromProject(
      @PathVariable Long projectId, @PathVariable Long analyzerConfigurationId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    analyzerConfigurationRepository.deleteByProjectIdAndId(projectId, analyzerConfigurationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(produces = "application/hal+json")
  public ResponseEntity<List<AnalyzerConfigurationResource>>
      getAnalyzerConfigurationsForProject(@PathVariable Long projectId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    List<AnalyzerConfiguration> configurations = analyzerConfigurationRepository.findByProjectId(projectId);
    AnalyzerConfigurationResourceAssembler assembler = new AnalyzerConfigurationResourceAssembler(projectId);
    return new ResponseEntity<>(assembler.toResourceList(configurations), HttpStatus.OK);
  }

  @GetMapping(path = "/{analyzerConfigurationId}")
  public ResponseEntity<AnalyzerConfigurationResource> getSingleAnalyzerConfigurationForProject(
      @PathVariable Long projectId, @PathVariable Long analyzerConfigurationId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    AnalyzerConfiguration configuration =
        analyzerConfigurationRepository.findByProjectIdAndId(projectId, analyzerConfigurationId);
    if (configuration == null) {
      throw new ResourceNotFoundException();
    }
    AnalyzerConfigurationResourceAssembler assembler =
        new AnalyzerConfigurationResourceAssembler(projectId);
    return new ResponseEntity<>(assembler.toResource(configuration), HttpStatus.OK);
  }

  @PostMapping(path = "/{analyzerConfigurationId}")
  public ResponseEntity<AnalyzerConfigurationResource> updateAnalyzerConfiguration(
      @PathVariable Long projectId,
      @PathVariable Long analyzerConfigurationId,
      @RequestBody @Valid AnalyzerConfigurationResource resource) {
    analyzerVerifier.checkAnalyzerExistsOrThrowException(resource.getAnalyzerName());
    AnalyzerConfigurationResourceAssembler assembler =
        new AnalyzerConfigurationResourceAssembler(projectId);
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    AnalyzerConfiguration entity =
        analyzerConfigurationRepository.findByProjectIdAndId(projectId, analyzerConfigurationId);
    AnalyzerConfiguration savedEntity = assembler.updateEntity(resource, project, entity);
    return new ResponseEntity<>(assembler.toResource(savedEntity), HttpStatus.OK);
  }
}
