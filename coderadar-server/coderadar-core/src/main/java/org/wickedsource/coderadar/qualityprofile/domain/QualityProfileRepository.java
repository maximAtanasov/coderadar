package org.wickedsource.coderadar.qualityprofile.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QualityProfileRepository extends CrudRepository<QualityProfile, Long> {

  List<QualityProfile> findByProjectId(Long projectId);
}
