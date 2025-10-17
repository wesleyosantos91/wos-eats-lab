package io.github.wesleyosantos91.catalog.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Adapter {

    AdapterType type() default AdapterType.INBOUND;

    String description() default "";

    @AliasFor(annotation = Component.class)
    String value() default "";

    enum AdapterType {
        INBOUND,
        OUTBOUND
    }
}
