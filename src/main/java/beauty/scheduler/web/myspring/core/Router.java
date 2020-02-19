package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.Endpoint;
import beauty.scheduler.web.myspring.RequestMethod;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.*;
import static beauty.scheduler.web.myspring.core.ParamHelper.getAllParameters;

public class Router {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private BeanFactory beanFactory;
    private Map<String, Endpoint> endpoints;
    private Endpoint notFoundEdnpoint;

    public static void route(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext context = req.getServletContext();
        Router router = (Router) context.getAttribute(ATTR_ROUTER);
        router.process(req, resp);
    }

    public Router(BeanFactory beanFactory, Map<String, Endpoint> endpoints, Endpoint notFoundEdnpoint) {
        this.endpoints = endpoints;
        this.beanFactory = beanFactory;
        this.notFoundEdnpoint = notFoundEdnpoint;
    }

    //returns true if request has to be filtered and not go further
    public boolean restricted(HttpServletRequest req, HttpServletResponse resp) {
        Role sessionRole = Security.getUserPrincipal(req).getRole();

        Endpoint endpoint = determineEndpoint(req);

        if (endpoint.hasExceptionForRole(sessionRole)) {
            processException(req, resp, endpoint, sessionRole);
            return true;
        } else if (endpoint.hasRedirectionForRole(sessionRole)) {
            processRedirect(req, resp, endpoint, sessionRole);
            return true;
        }

        return false;
    }

    private String process(HttpServletRequest req, HttpServletResponse resp) {
        Endpoint endpoint = (Endpoint) req.getAttribute(ATTR_ENDPOINT);

        String resultPage;

        try {
            resultPage = processController(req, resp, endpoint);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("processController " + endpoint.toString());
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception"; //for debug purposes
        }

        return processResultPage(resultPage, req, resp, endpoint);
    }

    private String processController(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint) throws InvocationTargetException, IllegalAccessException {
        Method method = endpoint.getMethod();

        //it's almost impossible, but for reliability and with keeping in ming that "notFoundEdnpoint" has such value of method, lets's do this check
        if (method == null) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception"; //for debug purposes
        }

        if (endpoint.getUrlPattern().contains("{")) {
            try {
                addURIParametersToRequest(endpoint.getUrlPattern(), req);
            } catch (NoSuchFieldException e) {
                processException(req, resp, ExceptionKind.WRONG_DATA_PASSED);
                return "exception"; //for debug purposes
            }
        }

        Object[] args;
        try {
            args = getAllParameters(endpoint, req, resp);
        } catch (Exception e) {
            processException(req, resp, ExceptionKind.WRONG_DATA_PASSED);
            return "exception"; //for debug purposes
        }

        Object classInstance = beanFactory.getInstantiatedClass(method.getDeclaringClass().getSimpleName());

        return (String) method.invoke(classInstance, args);
    }

    private String processResultPage(String resultPage, HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint) {
        if (resultPage == null) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception"; //for debug purposes
        }

        if (resultPage.equals(DEFAULT_TEMPLATE)) {
            String defaultTemplate = endpoint.getDefaultTemplateForRole(Security.getUserPrincipal(req).getRole());

            if (StringUtils.isEmpty(defaultTemplate)) {
                processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
                return "exception"; //for debug purposes
            }

            resultPage = defaultTemplate;
        }

        if (resultPage.contains(REDIRECT)) {
            processRedirect(req, resp, resultPage.replace(REDIRECT, ""));
            return "redirect"; //for debug purposes
        }

        if (!"".equals(resultPage)) { //"" - for REST
            try {
                req.getRequestDispatcher(resultPage).forward(req, resp);
            } catch (IOException | ServletException e) {
                processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            }
        }

        return resultPage;
    }

    public static String sendRESTData(Object data, HttpServletResponse resp) throws ExtendedException {
        resp.setContentType("application/json");

        PrintWriter out;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            throw new ExtendedException(ExceptionKind.REPOSITORY_ISSUE);
        }

        String jsonData = new Gson().toJson(data);
        out.print(jsonData);
        out.flush();

        //NOTE: leave blank for REST API
        return "";
    }

    private void processRedirect(HttpServletRequest req, HttpServletResponse resp, String redirectTo) {
        try {
            resp.sendRedirect(redirectTo);
        } catch (IOException e) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
        }
    }

    private void processRedirect(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
        String redirectTo = endpoint.getRedirectionForRole(role);

        if (StringUtils.isEmpty(redirectTo)) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return;
        }

        processRedirect(req, resp, redirectTo);
    }

    private void processException(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
        processException(req, resp, endpoint.getExceptionForRole(role));
    }

    private void processException(HttpServletRequest req, HttpServletResponse resp, ExceptionKind exceptionKind) {
        if (exceptionKind == null) {
            exceptionKind = ExceptionKind.PAGE_NOT_FOUND;
        }

        if (exceptionKind.equals(ExceptionKind.ACCESS_DENIED)) {
            Security.logOut(req);
        }

        try {
            req.setAttribute("exception", exceptionKind); //TODO maybe find better solution, if any
            resp.sendError(exceptionKind.getStatusCode(), exceptionKind.getErrorMessage());
        } catch (IOException e) {
            LOGGER.error("Curious: exceptionn during sending an error");
        }
    }

    private static void addURIParametersToRequest(String urlPattern, HttpServletRequest req) throws NoSuchFieldException {
        String[] patternParts = urlPattern.split("/");
        String[] uriParts = req.getRequestURI().split("/");

        if (patternParts.length != uriParts.length) {
            //it's impossible, but better to check
            throw new NoSuchFieldException("disparate URI and pattern!");
        }

        for (int i = 0; i <= patternParts.length - 1; i++) {
            String patternPart = patternParts[i];
            if (!patternPart.contains("{")) {
                continue;
            }
            String attributeName = patternPart.replaceAll("[{}]", "");
            req.setAttribute(URI_PREFIX + attributeName, uriParts[i]);
        }
    }

    private Endpoint determineEndpoint(HttpServletRequest req) {
        Endpoint endpoint = getEndpointForReguest(req);

        req.setAttribute(ATTR_ENDPOINT, endpoint);

        return endpoint;
    }

    private Endpoint getEndpointForReguest(HttpServletRequest req) {
        RequestMethod requestMethod = RequestMethod.valueOf(req.getMethod());
        String requestURI = req.getRequestURI();

        //first quick attempt to search:
        String endpointKey = Endpoint.endpointKey(requestMethod, requestURI);

        if (endpoints.containsKey(endpointKey)) {
            return endpoints.get(endpointKey);
        }

        //more complex search:
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
}