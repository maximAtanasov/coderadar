package org.wickedsource.coderadar.analyzer.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wickedsource.coderadar.analyzer.service.AnalyzerPluginRegistry;

@Controller
@Transactional
@RequestMapping(path = "/analyzers")
public class AnalyzerController {

  private AnalyzerPluginRegistry analyzerRegistry;

  @Autowired
  public AnalyzerController(AnalyzerPluginRegistry analyzerRegistry) {
    this.analyzerRegistry = analyzerRegistry;
  }

  @SuppressWarnings("unchecked")
  @GetMapping(produces = "application/hal+json")
  public ResponseEntity<List<AnalyzerResource>> listAnalyzers() {
    return new ResponseEntity<>(
        new AnalyzerResourceAssembler().toResourceList(analyzerRegistry.getAvailableAnalyzers()),
        HttpStatus.OK);
  }
}
