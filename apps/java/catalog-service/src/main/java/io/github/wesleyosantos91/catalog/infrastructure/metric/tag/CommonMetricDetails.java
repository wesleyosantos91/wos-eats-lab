package io.github.wesleyosantos91.catalog.infrastructure.metric.tag;

import io.micrometer.core.instrument.MeterRegistry;

public abstract class CommonMetricDetails {

    protected String name;
    protected MeterRegistry registry;

    protected CommonMetricDetails(String name, MeterRegistry registry) {
        this.name = name;
        this.registry = registry;
    }
}
