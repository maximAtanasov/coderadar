package org.wickedsource.coderadar.job.execute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wickedsource.coderadar.CoderadarConfiguration;
import org.wickedsource.coderadar.job.domain.Job;
import org.wickedsource.coderadar.job.domain.ScanCommitJob;
import org.wickedsource.coderadar.job.domain.ScanVcsJob;
import org.wickedsource.coderadar.job.execute.scan.commit.CommitScanner;
import org.wickedsource.coderadar.job.execute.scan.repository.VcsRepositoryScanner;

@Service
class JobExecutor {

    private VcsRepositoryScanner vcsScanner;

    private CommitScanner commitScanner;

    private CoderadarConfiguration config;

    @Autowired
    public JobExecutor(VcsRepositoryScanner vcsScanner, CommitScanner commitScanner, CoderadarConfiguration config) {
        this.vcsScanner = vcsScanner;
        this.commitScanner = commitScanner;
        this.config = config;
    }

    void execute(Job job) {
        if (config.isSlave()) {
            if (job instanceof ScanVcsJob) {
                vcsScanner.scan(((ScanVcsJob) job).getProjectId());
            } else if (job instanceof ScanCommitJob) {
                commitScanner.scan(((ScanCommitJob) job).getCommitId());
            } else {
                throw new IllegalArgumentException(String.format("unsupported job type %s", job.getClass()));
            }
        }
    }

}
