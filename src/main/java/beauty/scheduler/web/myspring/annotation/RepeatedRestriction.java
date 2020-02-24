package beauty.scheduler.web.myspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to make possible repetitive @Restriction annotation
 *
 * @author Victor Hilonenko
 * @see <a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code><strong>see more details here</strong></code></a>
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatedRestriction {
    Restriction[] value();
}