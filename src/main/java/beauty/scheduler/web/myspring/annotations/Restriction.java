package beauty.scheduler.web.myspring.annotations;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(RepeatedRestriction.class)
public @interface Restriction {
    Role role();

    String redirection() default "";

    ExceptionKind exception() default ExceptionKind.NULL;
}