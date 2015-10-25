package org.wickedsource.coderadar.analyzer.plugin.api;

import org.junit.Assert;
import org.junit.Test;
import org.wickedsource.coderadar.analyzer.api.FileMetrics;
import org.wickedsource.coderadar.analyzer.api.Metric;

public class FileMetricsTest {

    private static final Metric METRIC1 = new Metric("metric1");

    private static final Metric METRIC2 = new Metric("metric2");

    @Test
    public void testAdd() throws Exception {
        FileMetrics fileMetrics1 = new FileMetrics();
        fileMetrics1.setMetricCount(METRIC1, 500l);
        fileMetrics1.setMetricCount(METRIC2, 250l);

        FileMetrics fileMetrics2 = new FileMetrics();
        fileMetrics2.setMetricCount(METRIC1, 300l);

        fileMetrics1.add(fileMetrics2);

        Assert.assertEquals(800l, (long) fileMetrics1.getMetricCount(METRIC1));
        Assert.assertEquals(250l, (long) fileMetrics1.getMetricCount(METRIC2));
    }
}