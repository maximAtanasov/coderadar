package io.reflectoring.coderadar.projectadministration.analyzerconfig;

import io.reflectoring.coderadar.projectadministration.domain.AnalyzerConfiguration;
import io.reflectoring.coderadar.projectadministration.port.driven.analyzerconfig.GetAnalyzerConfigurationPort;
import io.reflectoring.coderadar.projectadministration.port.driver.analyzerconfig.get.GetAnalyzerConfigurationResponse;
import io.reflectoring.coderadar.projectadministration.service.analyzerconfig.GetAnalyzerConfigurationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

class GetAnalyzerConfigurationServiceTest {
  private GetAnalyzerConfigurationPort port = mock(GetAnalyzerConfigurationPort.class);

  @Test
  void returnsAnalyzerConfigurationWithIdOne() {
    GetAnalyzerConfigurationService testSubject = new GetAnalyzerConfigurationService(port);

    AnalyzerConfiguration analyzerConfiguration = new AnalyzerConfiguration();
    analyzerConfiguration.setId(1L);
    analyzerConfiguration.setAnalyzerName("analyzer");
    analyzerConfiguration.setEnabled(true);
    Mockito.when(port.getAnalyzerConfiguration(1L)).thenReturn(analyzerConfiguration);

    GetAnalyzerConfigurationResponse response = testSubject.getSingleAnalyzerConfiguration(1L);

    Assertions.assertEquals(analyzerConfiguration.getAnalyzerName(), response.getAnalyzerName());
    Assertions.assertEquals(analyzerConfiguration.getEnabled(), response.getEnabled());
    Assertions.assertEquals(analyzerConfiguration.getId(), response.getId());
  }
}