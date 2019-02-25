package org.wickedsource.coderadar.qualityprofile.rest;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wickedsource.coderadar.core.rest.validation.ResourceNotFoundException;
import org.wickedsource.coderadar.project.domain.Project;
import org.wickedsource.coderadar.project.rest.ProjectVerifier;
import org.wickedsource.coderadar.qualityprofile.domain.QualityProfile;
import org.wickedsource.coderadar.qualityprofile.domain.QualityProfileRepository;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/qualityprofiles")
public class QualityProfileController {

  private ProjectVerifier projectVerifier;

  private QualityProfileRepository qualityProfileRepository;

  @Autowired
  public QualityProfileController(
      ProjectVerifier projectVerifier, QualityProfileRepository qualityProfileRepository) {
    this.projectVerifier = projectVerifier;
    this.qualityProfileRepository = qualityProfileRepository;
  }

  @PostMapping(produces = "application/hal+json")
  public ResponseEntity<QualityProfileResource> createQualityProfile(
      @RequestBody @Valid QualityProfileResource qualityProfileResource,
      @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    QualityProfileResourceAssembler assembler = new QualityProfileResourceAssembler(project);
    QualityProfile profile = assembler.updateEntity(qualityProfileResource, new QualityProfile());
    profile = qualityProfileRepository.save(profile);
    return new ResponseEntity<>(assembler.toResource(profile), HttpStatus.CREATED);
  }

  @PostMapping(path = "/{profileId}", produces = "application/hal+json")
  public ResponseEntity<QualityProfileResource> updateQualityProfile(
      @Valid @RequestBody QualityProfileResource qualityProfileResource,
      @PathVariable Long profileId,
      @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    QualityProfileResourceAssembler assembler = new QualityProfileResourceAssembler(project);
    QualityProfile profile = qualityProfileRepository.findOne(profileId);
    if (profile == null) {
      throw new ResourceNotFoundException();
    }
    profile = assembler.updateEntity(qualityProfileResource, profile);
    profile = qualityProfileRepository.save(profile);
    return new ResponseEntity<>(assembler.toResource(profile), HttpStatus.OK);
  }

  @GetMapping(path = "/{profileId}", produces = "application/hal+json")
  public ResponseEntity<QualityProfileResource> getQualityProfile(
      @PathVariable Long profileId, @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    QualityProfileResourceAssembler assembler = new QualityProfileResourceAssembler(project);
    QualityProfile profile = qualityProfileRepository.findOne(profileId);
    if (profile == null) {
      throw new ResourceNotFoundException();
    }
    return new ResponseEntity<>(assembler.toResource(profile), HttpStatus.OK);
  }

  @DeleteMapping(path = "/{profileId}", produces = "application/hal+json")
  public ResponseEntity<String> deleteQualityProfile(
      @PathVariable Long profileId, @PathVariable Long projectId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    qualityProfileRepository.delete(profileId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  @GetMapping(produces = "application/hal+json")
  public ResponseEntity<List<QualityProfileResource>> listQualityProfiles(
      @PathVariable long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    List<QualityProfile> profilesPage = qualityProfileRepository.findByProjectId(projectId);
    QualityProfileResourceAssembler assembler = new QualityProfileResourceAssembler(project);
    List<QualityProfileResource> pagedResources = assembler.toResourceList(profilesPage);
    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }
}
