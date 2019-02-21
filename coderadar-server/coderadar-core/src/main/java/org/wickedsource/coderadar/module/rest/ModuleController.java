package org.wickedsource.coderadar.module.rest;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wickedsource.coderadar.core.rest.validation.ResourceNotFoundException;
import org.wickedsource.coderadar.core.rest.validation.UserException;
import org.wickedsource.coderadar.module.domain.Module;
import org.wickedsource.coderadar.module.domain.ModuleRepository;
import org.wickedsource.coderadar.project.domain.Project;
import org.wickedsource.coderadar.project.rest.ProjectVerifier;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/modules")
public class ModuleController {

  private ProjectVerifier projectVerifier;

  private ModuleRepository moduleRepository;

  private ModuleAssociationService moduleAssociationService;

  @Autowired
  public ModuleController(
      ProjectVerifier projectVerifier,
      ModuleRepository moduleRepository,
      ModuleAssociationService moduleAssociationService) {
    this.projectVerifier = projectVerifier;
    this.moduleRepository = moduleRepository;
    this.moduleAssociationService = moduleAssociationService;
  }

  @PostMapping
  public ResponseEntity<ModuleResource> createModule(
      @Valid @RequestBody ModuleResource moduleResource, @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    if (moduleRepository.countByProjectIdAndPath(projectId, moduleResource.getModulePath()) > 0) {
      throw new UserException(
          String.format(
              "Module with path '%s' already exists for this project!",
              moduleResource.getModulePath()));
    }
    ModuleResourceAssembler assembler = new ModuleResourceAssembler(project);
    Module module = new Module();
    assembler.updateEntity(module, moduleResource);
    module = moduleRepository.save(module);
    moduleAssociationService.associate(module);
    return new ResponseEntity<>(assembler.toResource(module), HttpStatus.CREATED);
  }

  @GetMapping(path = "/{moduleId}")
  public ResponseEntity<ModuleResource> getModule(
      @PathVariable Long moduleId, @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    Module module = moduleRepository.findByIdAndProjectId(moduleId, projectId);
    if (module == null) {
      throw new ResourceNotFoundException();
    }
    ModuleResourceAssembler assembler = new ModuleResourceAssembler(project);
    ModuleResource resource = assembler.toResource(module);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @PostMapping(path = "/{moduleId}")
  public ResponseEntity<ModuleResource> updateModule(
      @Valid @RequestBody ModuleResource moduleResource,
      @PathVariable Long moduleId,
      @PathVariable Long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    Module module = moduleRepository.findByIdAndProjectId(moduleId, projectId);
    if (module == null) {
      throw new ResourceNotFoundException();
    }
    ModuleResourceAssembler assembler = new ModuleResourceAssembler(project);
    module = assembler.updateEntity(module, moduleResource);
    moduleAssociationService.reassociate(module);
    return new ResponseEntity<>(assembler.toResource(module), HttpStatus.OK);
  }

  @SuppressWarnings("unchecked")
  @GetMapping
  public ResponseEntity<List<ModuleResource>> listModules(@PathVariable long projectId) {
    Project project = projectVerifier.loadProjectOrThrowException(projectId);
    List<Module> page = moduleRepository.findByProjectId(projectId);
    ModuleResourceAssembler assembler = new ModuleResourceAssembler(project);
    List<ModuleResource> pagedResources = assembler.toResourceList(page);
    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }

  @DeleteMapping(path = "/{moduleId}")
  public ResponseEntity<String> deleteModule(
      @PathVariable Long moduleId, @PathVariable Long projectId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    Module module = moduleRepository.findOne(moduleId);
    if (module == null) {
      throw new ResourceNotFoundException();
    }
    moduleAssociationService.disassociate(module);
    moduleRepository.delete(module);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
