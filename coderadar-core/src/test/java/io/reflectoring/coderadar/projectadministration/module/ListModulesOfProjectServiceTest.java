package io.reflectoring.coderadar.projectadministration.module;

import io.reflectoring.coderadar.projectadministration.domain.Module;
import io.reflectoring.coderadar.projectadministration.domain.Project;
import io.reflectoring.coderadar.projectadministration.port.driven.module.ListModulesOfProjectPort;
import io.reflectoring.coderadar.projectadministration.port.driven.project.GetProjectPort;
import io.reflectoring.coderadar.projectadministration.port.driver.module.get.GetModuleResponse;
import io.reflectoring.coderadar.projectadministration.service.module.ListModulesOfProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

class ListModulesOfProjectServiceTest {
  private ListModulesOfProjectPort port = mock(ListModulesOfProjectPort.class);
  private GetProjectPort getProjectPort = mock(GetProjectPort.class);

  @Test
  void returnsTwoModulesFromProject() {
    ListModulesOfProjectService testSubject = new ListModulesOfProjectService(port);

    Mockito.when(getProjectPort.get(anyLong())).thenReturn(new Project());

    Project project = new Project();
    project.setId(1L);

    List<Module> modules = new ArrayList<>();
    Module module1 = new Module();
    module1.setId(1L);
    module1.setPath("module-path-one");
    Module module2 = new Module();
    module2.setId(2L);
    module2.setPath("module-path-two");

    modules.add(module1);
    modules.add(module2);

    Mockito.when(port.listModules(project.getId())).thenReturn(modules);

    List<GetModuleResponse> response = testSubject.listModules(project.getId());

    Assertions.assertEquals(modules.size(), response.size());
    Assertions.assertEquals(module1.getId(), response.get(0).getId());
    Assertions.assertEquals(module1.getPath(), response.get(0).getPath());
    Assertions.assertEquals(module2.getId(), response.get(1).getId());
    Assertions.assertEquals(module2.getPath(), response.get(1).getPath());
  }
}