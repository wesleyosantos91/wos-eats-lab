package io.github.wesleyosantos91.catalog.infrastructure.metric.tag.timer;

import io.github.wesleyosantos91.catalog.infrastructure.metric.tag.CommonMetricDetails;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiTaggedTimer extends CommonMetricDetails {

    private static final double P50 = 0.5;
    private static final double P75 = 0.75;
    private static final double P90 = 0.9;
    private static final double P95 = 0.95;
    private static final double P99 = 0.99;

    List<String> tagNames;

    public MultiTaggedTimer(String name, MeterRegistry registry, String... tags) {
        super(name, registry);
        this.tagNames = Arrays.asList(tags.clone());
    }

    public Timer getTimer(String... tagValues) {
        final List<String> adaptedValues = Arrays.asList(tagValues);

        if (adaptedValues.size() != tagNames.size()) {
            throw new IllegalArgumentException("Timer tag values mismatch the tag names! "
                    + "Expected args are " + tagNames + ", provided tags are " + adaptedValues);
        }

        final int size = tagNames.size();
        final List<Tag> tags = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            tags.add(new ImmutableTag(tagNames.get(i), tagValues[i]));
        }

        return Timer.builder(name)
                .tags(tags)
                .publishPercentileHistogram()
                .publishPercentiles(P50, P75, P90, P95, P99)
                .register(registry);
    }
}
