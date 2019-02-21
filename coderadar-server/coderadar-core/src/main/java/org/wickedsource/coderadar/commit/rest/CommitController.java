package org.wickedsource.coderadar.commit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.wickedsource.coderadar.commit.domain.Commit;
import org.wickedsource.coderadar.commit.domain.CommitRepository;

import java.util.List;

@Controller
@Transactional
@RequestMapping(path = "/projects/{projectId}/commits")
public class CommitController {

  private CommitRepository commitRepository;

  @Autowired
  public CommitController(CommitRepository commitRepository) {
    this.commitRepository = commitRepository;
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/hal+json")
  public ResponseEntity<List<CommitResource>> listCommits(
      @PageableDefault Pageable pageable,
      PagedResourcesAssembler<Commit> pagedResourcesAssembler,
      @PathVariable long projectId) {
    Page<Commit> commitsPage = commitRepository.findByProjectId(projectId, pageable);
    CommitResourceAssembler commitResourceAssembler = new CommitResourceAssembler(projectId);
    List<CommitResource> pagedResources = commitResourceAssembler.toResourceList(commitsPage);
    return new ResponseEntity<>(pagedResources, HttpStatus.OK);
  }
}
