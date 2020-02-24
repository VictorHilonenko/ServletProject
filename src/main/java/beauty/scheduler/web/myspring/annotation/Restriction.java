package beauty.scheduler.web.myspring.annotation;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;

import java.lang.annotation.*;

/**
 * This annotation is used on methods and provides annotation-driven security and behavior features of the web application. There are similar Spring functions in security configuration (it is provided by extending WebSecurityConfigurerAdapter abstract class which implements WebSecurityConfigurer interface). Besides, @Restriction annotation provides not only permissions/restrictions but also redirections.
 *
 * @author Victor Hilonenko
 * @see <a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code><strong>see more details here</strong></code></a>
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(RepeatedRestriction.class)
public @interface Restriction {
    Role role();

    String redirection() default "";

    ExceptionKind exception() default ExceptionKind.NULL;
}