package io.github.wesleyosantos91.catalog.infrastructure.metric.tag.timer;

import io.github.wesleyosantos91.catalog.infrastructure.metric.tag.CommonMetricDetails;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class TaggedTimer extends CommonMetricDetails {

    private static final double P50 = 0.5;
    private static final double P75 = 0.75;
    private static final double P90 = 0.9;
    private static final double P95 = 0.95;
    private static final double P99 = 0.99;

    private final String tagName;

    public TaggedTimer(String identifier, String tagName, MeterRegistry registry) {
        super(identifier, registry);
        this.tagName = tagName;
    }

    public Timer getTimer(String tagValue) {
        return Timer.builder(name)
                .tag(tagName, tagValue)
                .publishPercentileHistogram()
                .publishPercentiles(P50, P75, P90, P95, P99)
                .register(registry);
    }
}

