package beauty.scheduler.web.myspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used on method's parameters and provide an easy way to get JSON data in any controller method
 *
 * @author Victor Hilonenko
 * @see <a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code><strong>see more details here</strong></code></a>
 */


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {
}
