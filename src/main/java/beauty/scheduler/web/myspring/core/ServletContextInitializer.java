package beauty.scheduler.web.myspring.core;

import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.annotation.EndpointMethod;
import beauty.scheduler.web.myspring.enums.RequestMethod;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.*;

public class ServletContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletContextInitializer.class);

    public static void init(ServletContext context) {
        if (context.getAttribute(ATTR_ROUTER) != null) {
            LOGGER.error("attempt to call ServletContextInitializer.init() method twice!");
            throw new IllegalAccessError("It is forbidden to call this method twice!");
        }

        BeanFactory beanFactory;
        try {
            beanFactory = new BeanFactory(new HashMap<>());
        } catch (ExtendedException e) {
            LOGGER.error("critical error: wrongConfiguration!");
            throw new RuntimeException(LocaleUtils.getLocalizedMessage("error.wrongConfiguration", LocaleUtils.getDefaultLocale().getLanguage()));
        }

        Map<String, Endpoint> endpoints = gatherRoutingData();
        Endpoint notFoundEdnpoint = determineNotFoundEdnpoint(endpoints);

        context.setAttribute(ATTR_ROUTER, new Router(beanFactory, endpoints, notFoundEdnpoint));

        context.setAttribute(ATTR_ACTIVE_USERS, new HashSet<String>());
    }

    private static Map<String, Endpoint> gatherRoutingData() {
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

        return Collections.unmodifiableMap(endpoints);
    }

    private static Endpoint determineNotFoundEdnpoint(Map<String, Endpoint> endpoints) {
        String keyNotFoundEdnpoint = Endpoint.endpointKey(RequestMethod.GET, "");

        if (endpoints.containsKey(keyNotFoundEdnpoint)) {
            return endpoints.get(keyNotFoundEdnpoint);
        }

        return new Endpoint(null);
    }
}