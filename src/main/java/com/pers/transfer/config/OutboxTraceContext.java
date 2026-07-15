package com.pers.transfer.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OutboxTraceContext {

    private static final String TRACE_PARENT = "traceparent";

    private final Tracer tracer;
    private final Propagator propagator;

    public String captureTraceParent() {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan == null) {
            return null;
        }

        Map<String, String> carrier = new HashMap<>();
        propagator.inject(currentSpan.context(), carrier, Map::put);
        return carrier.get(TRACE_PARENT);
    }

    public Span startSpan(String traceParent, String name) {
        if (traceParent == null || traceParent.isBlank()) {
            return tracer.nextSpan().name(name).start();
        }

        Map<String, String> carrier = Map.of(TRACE_PARENT, traceParent);
        return propagator.extract(carrier, (source, key) -> source.get(key))
                .name(name)
                .start();
    }
}
