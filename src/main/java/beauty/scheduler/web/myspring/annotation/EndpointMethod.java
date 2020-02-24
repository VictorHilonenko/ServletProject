package beauty.scheduler.web.myspring.annotation;

import beauty.scheduler.web.myspring.enums.ContentType;
import beauty.scheduler.web.myspring.enums.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on methods and maps HTTP requests to handler methods of JSP/HTML and REST/JSON endpoints. The Spring analogs for that is @RequestMapping and @RestController.
 *
 * @author Victor Hilonenko
 * @see <a href="https://github.com/VictorHilonenko/ServletProject/blob/master/src/main/java/beauty/scheduler/web/myspring/README.MD"><code><strong>see more details here</strong></code></a>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointMethod {
    RequestMethod requestMethod();

    String urlPattern();

    ContentType contentType() default ContentType.HTML;
}
