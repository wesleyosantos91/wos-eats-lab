package io.github.wesleyosantos91.catalog.infrastructure.metric.adapter;

import io.github.wesleyosantos91.catalog.core.annotation.Adapter;
import io.github.wesleyosantos91.catalog.domain.port.out.metric.MetricsPort;
import io.github.wesleyosantos91.catalog.domain.exception.MetricOperationException;
import io.github.wesleyosantos91.catalog.infrastructure.metric.tag.counter.MultiTaggedCounter;
import io.github.wesleyosantos91.catalog.infrastructure.metric.tag.timer.MultiTaggedTimer;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Adapter(type = Adapter.AdapterType.OUTBOUND, description = "Metrics Adapter")
public record MetricsAdapter(MeterRegistry meterRegistry) implements MetricsPort {

    private static final Log LOGGER = LogFactory.getLog(MetricsAdapter.class);

    public void incrementCounter(String name, String... tags) {
        try {
            meterRegistry.counter(name, tags).increment();
        } catch (MetricOperationException e) {
            LOGGER.error("Erro ao incrementar o contador.", e);
        }
    }

    public long summary(String name, OffsetDateTime dateTime, String... tags) {
        final long delay = Duration.between(dateTime, OffsetDateTime.now()).toMillis();

        try {
            final DistributionSummary summary = meterRegistry.summary(name, tags);
            summary.record(delay);
        } catch (MetricOperationException e) {
            LOGGER.error("Erro ao obter o resumo.", e);
        }

        return delay;
    }

    public void summary(String name, long delay, String... tags) {

        try {
            final DistributionSummary summary = meterRegistry.summary(name, tags);
            summary.record(delay);
        } catch (MetricOperationException e) {
            LOGGER.error("Erro ao obter o resumo.", e);
        }
    }

    public void incrementMultiTaggedCounter(String name, String[] tagNames, String... tagValues) {
        try {
            final MultiTaggedCounter counter = new MultiTaggedCounter(name, meterRegistry, tagNames);
            counter.increment(tagValues);
        } catch (MetricOperationException e) {
            LOGGER.error("Erro ao incrementar MultiTaggedCounter.", e);
        }
    }

    public void recordMultiTaggedTimer(String name, String[] tagNames, long durationMillis, String... tagValues) {
        try {
            final MultiTaggedTimer timer = new MultiTaggedTimer(name, meterRegistry, tagNames);
            final Timer t = timer.getTimer(tagValues);
            t.record(Duration.ofMillis(durationMillis));
        } catch (MetricOperationException e) {
            LOGGER.error("Erro ao registrar MultiTaggedTimer.", e);
        }
    }
}
