package beauty.scheduler.web.myspring.annotation;

import beauty.scheduler.web.myspring.ContentType;
import beauty.scheduler.web.myspring.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EndpointMethod {
    RequestMethod requestMethod();

    String urlPattern();

    ContentType contentType() default ContentType.HTML;
}
