package io.reflectoring.coderadar.graph.analyzer.service;

import io.reflectoring.coderadar.analyzer.domain.Finding;
import io.reflectoring.coderadar.analyzer.domain.MetricValue;
import io.reflectoring.coderadar.graph.analyzer.domain.CommitEntity;
import io.reflectoring.coderadar.graph.analyzer.domain.FileEntity;
import io.reflectoring.coderadar.graph.analyzer.domain.FindingEntity;
import io.reflectoring.coderadar.graph.analyzer.domain.MetricValueEntity;
import io.reflectoring.coderadar.graph.analyzer.repository.CommitRepository;
import io.reflectoring.coderadar.graph.analyzer.repository.FileRepository;
import io.reflectoring.coderadar.graph.analyzer.repository.MetricRepository;
import io.reflectoring.coderadar.projectadministration.port.driven.analyzer.SaveMetricPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveMetricAdapter implements SaveMetricPort {

  private MetricRepository metricRepository;

  private final CommitRepository commitRepository;
  private final FileRepository fileRepository;

  @Autowired
  public SaveMetricAdapter(
      MetricRepository metricRepository,
      CommitRepository commitRepository,
      FileRepository fileRepository) {
    this.metricRepository = metricRepository;
    this.commitRepository = commitRepository;
    this.fileRepository = fileRepository;
  }

  @Override
  public void saveMetricValues(List<MetricValue> metricValues, Long projectId) {

    // Get the commit entities of the metric values.
    List<Long> commitIds = new ArrayList<>();
    for (MetricValue metricValue : metricValues) {
      commitIds.add(metricValue.getCommit().getId());
    }
    List<CommitEntity> commitEntities = commitRepository.findCommitsByIds(commitIds);
    HashMap<Long, CommitEntity> commits = new HashMap<>();
    for (CommitEntity commitEntity : commitEntities) {
      commitEntity.setAnalyzed(true);
      commits.put(commitEntity.getId(), commitEntity);
    }

    // Get the file entities of the metric values.
    List<Long> fileIds = new ArrayList<>();
    for (MetricValue metricValue : metricValues) {
      fileIds.add(metricValue.getFileId());
    }
    List<FileEntity> fileEntities = fileRepository.findFilesByIds(fileIds);
    HashMap<Long, FileEntity> files = new HashMap<>();

    List<MetricValueEntity> metricValueEntities = new ArrayList<>();
    for (FileEntity fileEntity : fileEntities) {
      files.put(fileEntity.getId(), fileEntity);
    }

    for (MetricValue metricValue : metricValues) {
      MetricValueEntity metricValueEntity = new MetricValueEntity();

      // Set CommitEntity
      CommitEntity commitEntity = commits.get(metricValue.getCommit().getId());
      commitEntity.getMetricValues().add(metricValueEntity);
      metricValueEntity.setCommit(commitEntity);

      // Set FileEntity
      FileEntity fileEntity = files.get(metricValue.getFileId());
      metricValueEntity.setFile(fileEntity);

      // Set rest of attributes
      metricValueEntity.setFindings(mapFindingsToEntities(metricValue.getFindings()));
      metricValueEntity.setValue(metricValue.getValue());
      metricValueEntity.setName(metricValue.getName());
      metricValueEntities.add(metricValueEntity);
    }
    metricRepository.save(metricValueEntities, 1);
  }

  private List<FindingEntity> mapFindingsToEntities(List<Finding> findings) {
    List<FindingEntity> result = new ArrayList<>();
    for (Finding finding : findings) {
      FindingEntity findingEntity = new FindingEntity();
      findingEntity.setCharEnd(finding.getCharEnd());
      findingEntity.setCharStart(finding.getCharStart());
      findingEntity.setLineEnd(finding.getLineEnd());
      findingEntity.setLineStart(finding.getLineStart());
      result.add(findingEntity);
    }
    return result;
  }
}
