package org.wickedsource.coderadar.analyzingjob.rest;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wickedsource.coderadar.analyzingjob.domain.AnalyzingJob;
import org.wickedsource.coderadar.analyzingjob.domain.AnalyzingJobRepository;
import org.wickedsource.coderadar.analyzingjob.domain.ProjectResetException;
import org.wickedsource.coderadar.analyzingjob.domain.ProjectResetter;
import org.wickedsource.coderadar.core.rest.validation.ResourceNotFoundException;
import org.wickedsource.coderadar.core.rest.validation.UserException;
import org.wickedsource.coderadar.project.domain.Project;
import org.wickedsource.coderadar.project.rest.ProjectVerifier;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/analyzingJob")
public class AnalyzingJobController {

  private ProjectVerifier projectVerifier;

  private AnalyzingJobRepository analyzingJobRepository;

  private ProjectResetter projectResetter;

  @Autowired
  public AnalyzingJobController(
      ProjectVerifier projectVerifier,
      AnalyzingJobRepository analyzingJobRepository,
      ProjectResetter projectResetter) {
    this.projectVerifier = projectVerifier;
    this.analyzingJobRepository = analyzingJobRepository;
    this.projectResetter = projectResetter;
  }

  @GetMapping
  public ResponseEntity<AnalyzingJobResource> getAnalyzingJob(
      @PathVariable("projectId") Long projectId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    AnalyzingJob strategy = analyzingJobRepository.findByProjectId(projectId);
    if (strategy == null) {
      throw new ResourceNotFoundException();
    }
    AnalyzingJobResourceAssembler assembler = new AnalyzingJobResourceAssembler();
    return new ResponseEntity<>(assembler.toResource(strategy), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<AnalyzingJobResource> setAnalyzingJob(
      @PathVariable("projectId") Long projectId,
      @Valid @RequestBody AnalyzingJobResource resource) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);

    if (resource.isRescanNullsafe()) {
      try {
        projectResetter.resetProject(project);
      } catch (ProjectResetException e) {
        throw new UserException(
            "Cannot re-scan project while there are analyzer jobs waiting or running.");
      }
    }

    AnalyzingJob strategy = analyzingJobRepository.findByProjectId(projectId);
    if (strategy == null) {
      strategy = new AnalyzingJob();
    }

    AnalyzingJobResourceAssembler assembler = new AnalyzingJobResourceAssembler();
    strategy = assembler.updateEntity(strategy, resource, project);
    analyzingJobRepository.save(strategy);
    return new ResponseEntity<>(assembler.toResource(strategy), HttpStatus.OK);
  }
}
