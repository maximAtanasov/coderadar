package io.reflectoring.coderadar.rest.integration.module;

import io.reflectoring.coderadar.graph.projectadministration.domain.ModuleEntity;
import io.reflectoring.coderadar.graph.projectadministration.domain.ProjectEntity;
import io.reflectoring.coderadar.graph.projectadministration.module.repository.CreateModuleRepository;
import io.reflectoring.coderadar.graph.projectadministration.project.repository.CreateProjectRepository;
import io.reflectoring.coderadar.projectadministration.port.driver.module.get.GetModuleResponse;
import io.reflectoring.coderadar.rest.integration.ControllerTestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static io.reflectoring.coderadar.rest.integration.JsonHelper.fromJson;
import static io.reflectoring.coderadar.rest.integration.ResultMatchers.containsResource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class GetModuleControllerIntegrationTest extends ControllerTestTemplate {

  @Autowired private CreateProjectRepository createProjectRepository;

  @Autowired private CreateModuleRepository createModuleRepository;

  @Test
  void getModuleWithId() throws Exception {
    // Set up
    ProjectEntity testProject = new ProjectEntity();
    testProject.setVcsUrl("https://valid.url");
    testProject = createProjectRepository.save(testProject);

    ModuleEntity module = new ModuleEntity();
    module.setPath("test-module");
    module.setProject(testProject);
    module = createModuleRepository.save(module);

    // Test
    mvc()
        .perform(get("/projects/" + testProject.getId() + "/modules/" + module.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(containsResource(GetModuleResponse.class))
        .andDo(
            result -> {
              GetModuleResponse response =
                  fromJson(result.getResponse().getContentAsString(), GetModuleResponse.class);
              Assertions.assertEquals("test-module", response.getPath());
            })
            .andDo(document("modules/get"));

  }

  @Test
  void getModuleReturnsErrorWhenModuleNotFound() throws Exception {
    mvc()
        .perform(get("/projects/0/modules/0"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
            MockMvcResultMatchers.jsonPath("errorMessage").value("Module with id 0 not found."));
  }
}