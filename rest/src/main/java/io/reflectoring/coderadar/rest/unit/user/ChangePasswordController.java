package io.reflectoring.coderadar.rest.unit.user;

import io.reflectoring.coderadar.core.projectadministration.port.driver.user.password.ChangePasswordCommand;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.password.ChangePasswordUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangePasswordController {
  private final ChangePasswordUseCase changePasswordUseCase;

  @Autowired
  public ChangePasswordController(ChangePasswordUseCase changePasswordUseCase) {
    this.changePasswordUseCase = changePasswordUseCase;
  }

  @PostMapping(path = "/user/password/change")
  public ResponseEntity<String> changePassword(@RequestBody @Validated ChangePasswordCommand command) {
    try {
      changePasswordUseCase.changePassword(command);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (AuthenticationException e){
      return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
