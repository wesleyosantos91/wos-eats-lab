package io.github.wesleyosantos91.catalog.infrastructure.filter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationTraceMdcFilter implements Filter {

    public static final String HDR_CORRELATION = "X-Correlation-ID";
    public static final String HDR_TRACEPARENT = "traceparent";
    public static final int FOUR = 4;
    public static final int TWO = 2;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            final HttpServletRequest request = (HttpServletRequest) req;

            final String correlationId = Optional.ofNullable(request.getHeader(HDR_CORRELATION))
                    .filter(s -> !s.isBlank())
                    .orElse(UUID.randomUUID().toString());

            final Span current = Span.current();
            final SpanContext ctx = current.getSpanContext();

            final String traceId = ctx.isValid() ? ctx.getTraceId() : "";
            final String spanId = ctx.isValid() ? ctx.getSpanId() : "";
            final String sampled = ctx.isValid() && ctx.isSampled() ? "true" : "false";

            final String parentId = Optional.ofNullable(request.getHeader(HDR_TRACEPARENT))
                    .map(String::trim)
                    .filter(s -> s.split("-").length >= FOUR)
                    .map(s -> s.split("-")[TWO])
                    .orElse("");

            MDC.put("x-correlationID", correlationId);
            MDC.put("correlation_id", correlationId);

            MDC.put("traceId", traceId);
            MDC.put("spanId", spanId);
            MDC.put("parentId", parentId);
            MDC.put("sampled", sampled);

            chain.doFilter(req, res);

        } finally {
            MDC.clear();
        }
    }
}
