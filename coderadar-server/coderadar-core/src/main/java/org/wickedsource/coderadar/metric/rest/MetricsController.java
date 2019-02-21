package org.wickedsource.coderadar.metric.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wickedsource.coderadar.metric.domain.metricvalue.MetricValueRepository;
import org.wickedsource.coderadar.project.rest.ProjectVerifier;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/metrics")
public class MetricsController {

  private ProjectVerifier projectVerifier;

  private MetricValueRepository metricValueRepository;

  @Autowired
  public MetricsController(
      ProjectVerifier projectVerifier, MetricValueRepository metricValueRepository) {
    this.projectVerifier = projectVerifier;
    this.metricValueRepository = metricValueRepository;
  }

  @SuppressWarnings("unchecked")
  @GetMapping(produces = "application/hal+json")
  public ResponseEntity<List<MetricResource>> listMetrics(@PathVariable Long projectId) {
    projectVerifier.checkProjectExistsOrThrowException(projectId);
    List<String> metricsPage = metricValueRepository.findMetricsInProject(projectId);
    MetricResourceAssembler assembler = new MetricResourceAssembler();
    List<MetricResource> pagedResources = assembler.toResourceList(metricsPage);
    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }
}
