package io.reflectoring.coderadar.graph.projectadministration.module;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import io.reflectoring.coderadar.core.projectadministration.domain.Module;
import io.reflectoring.coderadar.core.projectadministration.domain.Project;
import io.reflectoring.coderadar.graph.projectadministration.module.repository.CreateModuleRepository;
import io.reflectoring.coderadar.graph.projectadministration.module.service.CreateModuleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("Create module")
class CreateModuleServiceTest {
  @Mock private CreateModuleRepository createModuleRepository;

  @InjectMocks private CreateModuleService createModuleService;

  @Test
  @DisplayName("Should return ID when saving a module")
  void shouldReturnIdWhenSavingAModule() {
    Project mockedProject = new Project();
    Module mockedItem = new Module();
    mockedItem.setId(1L);
    mockedItem.setProject(mockedProject);
    Module newItem = new Module();
    when(createModuleRepository.save(any(Module.class))).thenReturn(mockedItem);

    Long returnedId = createModuleService.createModule(newItem);

    verify(createModuleRepository, times(1)).save(newItem);
    verifyNoMoreInteractions(createModuleRepository);
    Assertions.assertEquals(new Long(1L), returnedId);
  }
}