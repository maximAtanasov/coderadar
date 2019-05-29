package io.reflectoring.coderadar.rest.integration.user;

import static io.reflectoring.coderadar.rest.integration.JsonHelper.fromJson;
import static io.reflectoring.coderadar.rest.integration.ResultMatchers.containsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import io.reflectoring.coderadar.core.projectadministration.domain.User;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.load.LoadUserResponse;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.login.LoginUserCommand;
import io.reflectoring.coderadar.graph.projectadministration.user.repository.RegisterUserRepository;
import io.reflectoring.coderadar.rest.integration.ControllerTestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class LoadUserControllerIntegrationTest extends ControllerTestTemplate {

  @Autowired private RegisterUserRepository registerUserRepository;

  @Test
  void loadUserWithId() throws Exception {
    User testUser = new User();
    testUser.setUsername("username2");
    testUser.setPassword("password1");
    testUser = registerUserRepository.save(testUser);

    final Long userId = testUser.getId();

    mvc()
        .perform(get("/user/" + userId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(containsResource(LoadUserResponse.class))
        .andDo(
            result -> {
              String a = result.getResponse().getContentAsString();
              LoadUserResponse response = fromJson(a, LoadUserResponse.class);
              Assertions.assertEquals("username2", response.getUsername());
              Assertions.assertEquals(userId, response.getId());
            })
            .andDo(document("user/get"));

  }

  @Test
  void loadUserWithIdOneReturnsErrorWhenUserNotFound() throws Exception {
    mvc()
        .perform(get("/user/1"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath("errorMessage").value("User with id 1 not found."));
  }



}
