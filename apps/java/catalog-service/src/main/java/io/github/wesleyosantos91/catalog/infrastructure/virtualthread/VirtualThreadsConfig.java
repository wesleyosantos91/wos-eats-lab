package io.github.wesleyosantos91.catalog.infrastructure.virtualthread;

import java.util.Map;
import java.util.concurrent.Executors;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class VirtualThreadsConfig {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreads() {
        return protocolHandler -> protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            final Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                final Map<String, String> previous = MDC.getCopyOfContextMap();
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    if (previous != null) {
                        MDC.setContextMap(previous);
                    } else {
                        MDC.clear();
                    }
                }
            };
        };
    }

    @Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public ThreadPoolTaskExecutor applicationTaskExecutor(TaskDecorator mdcTaskDecorator) {
        final ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setTaskDecorator(mdcTaskDecorator);
        exec.setThreadNamePrefix("vt-");
        exec.setCorePoolSize(1);
        exec.setMaxPoolSize(1);
        exec.initialize();
        return exec;
    }
}
