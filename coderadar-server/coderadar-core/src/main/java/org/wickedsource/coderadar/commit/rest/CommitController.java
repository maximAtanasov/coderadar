package org.wickedsource.coderadar.commit.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wickedsource.coderadar.commit.domain.Commit;
import org.wickedsource.coderadar.commit.domain.CommitRepository;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/commits")
public class CommitController {

  private CommitRepository commitRepository;

  @Autowired
  public CommitController(CommitRepository commitRepository) {
    this.commitRepository = commitRepository;
  }

  @GetMapping(produces = "application/hal+json")
  public ResponseEntity<List<CommitResource>> listCommits(@PathVariable long projectId) {
    List<Commit> commitsPage = commitRepository.findByProjectId(projectId);
    CommitResourceAssembler commitResourceAssembler = new CommitResourceAssembler();
    List<CommitResource> pagedResources = commitResourceAssembler.toResourceList(commitsPage);
    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }
}
