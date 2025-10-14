package io.github.wesleyosantos91.catalog.infrastructure.virtualthread;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.MDC;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VirtualThreadsConfig {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreads() {
        return protocolHandler -> protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public Executor mdcPreservingVThreadExecutor() {
        final Executor delegate = Executors.newVirtualThreadPerTaskExecutor();
        return command -> {
            final Map<String, String> contextMap = MDC.getCopyOfContextMap();
            delegate.execute(() -> {
                final Map<String, String> previous = MDC.getCopyOfContextMap();
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {
                    command.run();
                } finally {
                    if (previous != null) {
                        MDC.setContextMap(previous);
                    } else {
                        MDC.clear();
                    }
                }
            });
        };
    }
}
