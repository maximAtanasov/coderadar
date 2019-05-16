package io.reflectoring.coderadar.rest.unit.project;

import io.reflectoring.coderadar.core.projectadministration.ProjectNotFoundException;
import io.reflectoring.coderadar.core.projectadministration.port.driver.project.update.UpdateProjectCommand;
import io.reflectoring.coderadar.core.projectadministration.port.driver.project.update.UpdateProjectUseCase;
import java.net.MalformedURLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
public class UpdateProjectController {
  private final UpdateProjectUseCase updateProjectUseCase;

  @Autowired
  public UpdateProjectController(UpdateProjectUseCase updateProjectUseCase) {
    this.updateProjectUseCase = updateProjectUseCase;
  }

  @PostMapping(path = "/projects/{projectId}")
  public ResponseEntity<String> updateProject(
      @RequestBody @Validated UpdateProjectCommand command,
      @PathVariable(name = "projectId") Long projectId)
      throws MalformedURLException {
    try {
      updateProjectUseCase.update(command, projectId);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (ProjectNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}