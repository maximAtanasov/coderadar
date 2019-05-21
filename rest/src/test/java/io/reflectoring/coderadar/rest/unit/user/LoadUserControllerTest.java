package io.reflectoring.coderadar.rest.unit.user;

import io.reflectoring.coderadar.core.projectadministration.port.driver.user.load.LoadUserResponse;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.load.LoadUserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class LoadUserControllerTest {

  @Mock private LoadUserUseCase loadUserUseCase;
  @InjectMocks private LoadUserController testSubject;

  @Test
  void loadUserWithIdOne() {
    LoadUserResponse user = new LoadUserResponse(1L, "username");

    Mockito.when(loadUserUseCase.loadUser(1L)).thenReturn(user);

    ResponseEntity<LoadUserResponse> responseEntity = testSubject.loadUser(1L);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals("username", responseEntity.getBody().getUsername());
  }
}
