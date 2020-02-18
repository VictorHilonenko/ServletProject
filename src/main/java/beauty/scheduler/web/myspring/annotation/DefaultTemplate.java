package beauty.scheduler.web.myspring.annotation;

import beauty.scheduler.entity.enums.Role;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(RepeatedTemplate.class)
public @interface DefaultTemplate {
    Role role() default Role.all;

    String template();
}