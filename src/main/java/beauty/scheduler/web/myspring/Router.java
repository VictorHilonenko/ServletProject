package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.annotations.ParamName;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: not ready for review
//need a bit time to refactor this "god class"
public class Router {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private ClassFactory classFactory;
    private Map<String, Endpoint> endpoints;
    private Endpoint notFoundEdnpoint;

    public static void route(HttpServletRequest req, HttpServletResponse resp) {
        ServletContext context = req.getServletContext();
        Router router = (Router) context.getAttribute(ATTR_ROUTER);
        router.process(req, resp);
    }

    public Router(ClassFactory classFactory, Map<String, Endpoint> endpoints, Endpoint notFoundEdnpoint) {
        this.endpoints = endpoints;
        this.classFactory = classFactory;
        this.notFoundEdnpoint = notFoundEdnpoint;
    }

    private Endpoint getEndpointFor(RequestMethod requestMethod, String urlPattern) {
        return endpoints.getOrDefault(Endpoint.endpointKey(requestMethod, urlPattern), notFoundEdnpoint);
    }

    //returns true if request has to be filtered and not go further
    public boolean restricted(HttpServletRequest req, HttpServletResponse resp) {
        Role sessionRole = Security.getUserPrincipal(req).getRole();

        RequestMethod requestMethod = RequestMethod.valueOf(req.getMethod());
        String urlPattern = getPatternForURI(req.getRequestURI());
        Endpoint endpoint = getEndpointFor(requestMethod, urlPattern);

        req.setAttribute(ATTR_ENDPOINT, endpoint);

        if (endpoint.hasExceptionForRole(sessionRole)) {
            processException(req, resp, endpoint, sessionRole);
            return true;
        } else if (endpoint.hasRedirectionForRole(sessionRole)) {
            processRedirect(req, resp, endpoint, sessionRole);
            return true;
        }

        return false;
    }

    public String process(HttpServletRequest req, HttpServletResponse resp) {
        Endpoint endpoint = (Endpoint) req.getAttribute(ATTR_ENDPOINT);

        String resultPage;

        try {
            resultPage = processController(req, resp, endpoint);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("processController " + endpoint.toString());
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception"; //for debug purposes
        }

        //(!!!) тут може просто метод процедурно запускати, або продумати, що буде для тестів
        return processResultPage(resultPage, req, resp, endpoint);
    }

    private String processController(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint) throws InvocationTargetException, IllegalAccessException {
        Method method = endpoint.getMethod();

        if (method == null) { //it's possible for "notFoundEdnpoint"
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception"; //for debug purposes
        }

        Object[] args;

        try {
            args = getAllParameters(endpoint, req, resp);
        } catch (Exception e) {
            processException(req, resp, ExceptionKind.WRONG_DATA_PASSED);
            return "exception"; //for debug purposes
        }

        Object classInstance = classFactory.getInstantiatedClass(method.getDeclaringClass().getSimpleName());

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

    private void processRedirect(HttpServletRequest req, HttpServletResponse resp, String redirectTo) {
        try {
            resp.sendRedirect(redirectTo);
        } catch (IOException e) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
        }
    }

    public void processRedirect(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
        String redirectTo = endpoint.getRedirectionForRole(role);

        if (StringUtils.isEmpty(redirectTo)) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return;
        }

        processRedirect(req, resp, redirectTo);
    }

    public void processException(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
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

    private Object[] getAllParameters(Endpoint endpoint, HttpServletRequest req, HttpServletResponse resp) throws NoSuchMethodException, NoSuchFieldException, ExtendedException {
        Method method = endpoint.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        if (endpoint.getUrlPattern().contains("{")) {
            addURIParametersToRequest(endpoint.getUrlPattern(), req);
        }

        boolean allParametersSet = true;

        for (int i = 0; i <= parameters.length - 1; i++) {
            try {
                args[i] = getParameter(parameters[i], req, resp);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                allParametersSet = false;
            }
        }

        if (!allParametersSet) {
            throw new ExtendedException(ExceptionKind.WRONG_DATA_PASSED);
        }

        return args;
    }

    public static void addURIParametersToRequest(String urlPattern, HttpServletRequest req) throws NoSuchFieldException {
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

    private Object getParameter(Parameter parameter, HttpServletRequest req, HttpServletResponse resp) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, ExtendedException {
        if (parameter.isAnnotationPresent(ParamName.class)) {
            return getAnnotatedParameter(parameter, req, resp);
        } else {
            return getNonAnnotatedParameter(parameter.getParameterizedType(), req, resp);
        }
    }

    private Object getAnnotatedParameter(Parameter parameter, HttpServletRequest req, HttpServletResponse resp) throws NoSuchFieldException, ExtendedException {
        Object result = null;

        Type type = parameter.getParameterizedType();

        ParamName paramName = parameter.getDeclaredAnnotation(ParamName.class);
        String name = paramName.name();

        if (type == String.class) {
            result = getRequestValue(req, name);
        } else if (type == LocalDate.class) {
            try {
                result = LocalDate.parse(getRequestValue(req, name));
            } catch (DateTimeParseException e) {
                throw new ExtendedException(ExceptionKind.WRONG_DATA_PASSED);
            }
        } else if (type == Long.class) {
            try {
                result = Long.parseLong(getRequestValue(req, name));
            } catch (DateTimeParseException e) {
                throw new ExtendedException(ExceptionKind.WRONG_DATA_PASSED);
            }
        } else {
            //... other types ...
        }

        return result;
    }

    private Object getNonAnnotatedParameter(Type type, HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Object result;

        if (type == HttpServletRequest.class) {
            result = req;
        } else if (type == HttpServletResponse.class) {
            result = resp;
        } else {
            result = makeInstance((Class) type, req);
        }

        return result;
    }

    private Object makeInstance(Class aClass, HttpServletRequest req) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Object instance = aClass.newInstance();

        for (Field field : aClass.getDeclaredFields()) {
            String value = getRequestValue(req, field.getName());
            if (value == null) {
                continue;
            }
            ReflectUtils.set(instance, field.getName(), field.getType(), value);
        }

        return instance;
    }

    private String getRequestValue(HttpServletRequest req, String name) throws NoSuchFieldException {
        //try to find in request:
        if (req.getParameterMap().containsKey(name)) {
            return req.getParameter(name);
        }

        //try to find among URI attributes, they are already in req with URI_PREFIX prefix:
        if (hasAttribute(req.getAttributeNames(), URI_PREFIX + name)) {
            return (String) req.getAttribute(URI_PREFIX + name);
        }

        //try to find among session attributes
        HttpSession session = Security.getActualSession(req);
        if (hasAttribute(session.getAttributeNames(), name)) {
            return (String) session.getAttribute(name);
        }

        //there is a "feature" with radio buttons in JSP: if user did not select any, req.getParameterMap() has no such parameter then,
        //so, if we didn't found a value, we simply return null:

        return null;
    }

    private boolean hasAttribute(Enumeration<String> attributes, String name) {
        return Collections.list(attributes)
                .stream().anyMatch(s -> s.equals(name));
    }

    //NOTE: now this method is hardcoded, need to make proper solution
    public String getPatternForURI(String requestURI) {
        String urlPattern = requestURI;

        if (urlPattern.contains("/feedbacks/")) {
            urlPattern = "/feedbacks/{appointmentId}/{quickAccessCode}";
        }
        if (urlPattern.contains("/api/appointments/")) {
            urlPattern = "/api/appointments/{start}/{end}";
        }

        return urlPattern;
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
}