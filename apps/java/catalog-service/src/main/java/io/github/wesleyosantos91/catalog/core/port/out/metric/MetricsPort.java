package io.github.wesleyosantos91.catalog.core.port.out.metric;

import java.time.OffsetDateTime;

public interface MetricsPort {

    void incrementCounter(String name, String... tags);

 
    long summary(String name, OffsetDateTime dateTime, String... tags);

 
    void summary(String name, long delay, String... tags);

 
    void incrementMultiTaggedCounter(String name, String[] tagNames, String... tagValues);

 
    void recordMultiTaggedTimer(String name, String[] tagNames, long durationMillis, String... tagValues);
}
