package io.reflectoring.coderadar.graph.query.service;

import io.reflectoring.coderadar.analyzer.domain.MetricValueDTO;
import io.reflectoring.coderadar.query.port.driven.GetMetricValuesOfCommitPort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetMetricValuesOfCommitAdapter implements GetMetricValuesOfCommitPort {

  // TODO
  @Override
  public List<MetricValueDTO> get(String commitHash) {
    return new LinkedList<>();
  }
}