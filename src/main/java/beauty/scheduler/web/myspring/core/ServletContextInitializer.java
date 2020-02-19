package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.web.myspring.Endpoint;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.annotation.EndpointMethod;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.*;

public class ServletContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletContextInitializer.class);

    public static void init(ServletContext context) {
        BeanFactory beanFactory = new BeanFactory(new HashMap<>());

        Map<String, Endpoint> endpoints = gatherRoutingData();
        Endpoint notFoundEdnpoint = determineNotFoundEdnpoint(endpoints);

        context.setAttribute(ATTR_ROUTER, new Router(beanFactory, endpoints, notFoundEdnpoint));

        context.setAttribute(ATTR_ACTIVE_USERS, new HashSet<String>());
    }

    public static Map<String, Endpoint> gatherRoutingData() {
        Map<String, Endpoint> endpoints = new HashMap<>();

        Reflections refMethods = new Reflections(MAIN_PACKAGE, new MethodAnnotationsScanner());
        refMethods.getMethodsAnnotatedWith(EndpointMethod.class).forEach(aMethod -> {
            Endpoint endpoint = new Endpoint(aMethod);
            String endpointKey = endpoint.getEndpointKey();

            if (endpoints.containsKey(endpointKey)) {
                LOGGER.warn("wrong endpoints configuration, check: " + endpointKey);
                return;
            }

            endpoints.put(endpointKey, endpoint);

        });

        return endpoints;
    }

    private static Endpoint determineNotFoundEdnpoint(Map<String, Endpoint> endpoints) {
        String keyNotFoundEdnpoint = Endpoint.endpointKey(RequestMethod.GET, "");

        if (endpoints.containsKey(keyNotFoundEdnpoint)) {
            return endpoints.get(keyNotFoundEdnpoint);
        }

        Endpoint endpoint = new Endpoint(null);
        //(!!!) maybe pass to constructor
        endpoint.getExceptions().addValueForRole(Role.all, ExceptionKind.PAGE_NOT_FOUND);

        return endpoint;
    }
}