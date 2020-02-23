package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.enums.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.*;

public class Router {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private BeanFactory beanFactory;
    private Map<String, Endpoint> endpoints;
    private Endpoint notFoundEdnpoint;

    public static void route(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext context = req.getServletContext();
        Router router = (Router) context.getAttribute(ATTR_ROUTER);

        ProcessHelper.process(router, req, resp);
    }

    Router(BeanFactory beanFactory, Map<String, Endpoint> endpoints, Endpoint notFoundEdnpoint) {
        this.endpoints = endpoints;
        this.beanFactory = beanFactory;
        this.notFoundEdnpoint = notFoundEdnpoint;
    }

    public boolean restricted(HttpServletRequest req, HttpServletResponse resp) {
        Role sessionRole = Security.getUserPrincipal(req).getRole();

        Endpoint endpoint = determineEndpoint(req);

        if (endpoint.hasExceptionForRole(sessionRole)) {
            ProcessHelper.processException(req, resp, endpoint, sessionRole);
            return true;
        } else if (endpoint.hasRedirectionForRole(sessionRole)) {
            ProcessHelper.processRedirect(req, resp, endpoint, sessionRole);
            return true;
        }

        return false;
    }

    private Endpoint determineEndpoint(HttpServletRequest req) {
        Endpoint endpoint = getEndpointForReguest(req);

        req.setAttribute(ATTR_ENDPOINT, endpoint);

        return endpoint;
    }

    private Endpoint getEndpointForReguest(HttpServletRequest req) {
        RequestMethod requestMethod = RequestMethod.valueOf(req.getMethod());
        String requestURI = req.getRequestURI();

        String endpointKey = Endpoint.endpointKey(requestMethod, requestURI);

        if (endpoints.containsKey(endpointKey)) {
            return endpoints.get(endpointKey);
        }

        return findParametrizedEndpoint(requestMethod, requestURI);
    }

    private Endpoint findParametrizedEndpoint(RequestMethod requestMethod, String requestURI) {
        int slashesCount = StringUtils.count(requestURI, SLASH_SYMBOL);

        return endpoints.values().stream()
                .filter(endpoint -> (endpoint.getSlashesCount() == slashesCount &&
                        requestMethod.equals(endpoint.getRequestMethod()) &&
                        requestURI.startsWith(endpoint.getQuickUrlPattern())))
                .findFirst()
                .orElse(notFoundEdnpoint);
    }

    Object getBean(String simpleName) {
        return beanFactory.getInstantiatedClass(simpleName);
    }
}