package beauty.scheduler.web.myspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on classes that have to be injected as beans to other classes with @InjectDependency annotation. The Spring analogs for that are @Service and @Component annotations.
 *
 * @author Victor Hilonenko
 * @see <a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code><strong>see more details here</strong></code></a>
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceComponent {
}
