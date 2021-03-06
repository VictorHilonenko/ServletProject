package beauty.scheduler.web.myspring.core;

import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.web.myspring.annotation.Json;
import beauty.scheduler.web.myspring.annotation.ParamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

import static beauty.scheduler.util.AppConstants.URI_PREFIX;

class ParamHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamHelper.class);

    static Object[] getAllParameters(Endpoint endpoint, HttpServletRequest req, HttpServletResponse resp) {
        Method method = endpoint.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        boolean allParametersSet = true;

        for (int i = 0; i <= parameters.length - 1; i++) {
            try {
                args[i] = getParameter(parameters[i], req, resp);
            } catch (Exception e) {
                LOGGER.error("parameter " + parameters[i].getName() + " not set");
                allParametersSet = false;
            }
        }

        if (!allParametersSet) {
            LOGGER.error("not all parameters set to " + endpoint.toString());
        }

        return args;
    }

    private static Object getParameter(Parameter parameter, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (parameter.isAnnotationPresent(Json.class)) {
            return getJsonData(req);
        } else if (parameter.isAnnotationPresent(ParamName.class)) {
            return getAnnotatedParameter(parameter, req);
        } else {
            return getNonAnnotatedParameter(parameter.getParameterizedType(), req, resp);
        }
    }

    private static String getJsonData(HttpServletRequest req) throws IOException {
        return req
                .getReader()
                .lines()
                .collect(Collectors.joining());
    }

    private static Object getAnnotatedParameter(Parameter parameter, HttpServletRequest req) throws Exception {
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
        } else {
            //... other types ...
        }

        return result;
    }

    private static Object getNonAnnotatedParameter(Type type, HttpServletRequest req, HttpServletResponse resp) throws Exception {
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

    private static String getRequestValue(HttpServletRequest req, String name) {
        if (req.getParameterMap().containsKey(name)) {
            return req.getParameter(name);
        }

        if (hasAttribute(req.getAttributeNames(), URI_PREFIX + name)) {
            return (String) req.getAttribute(URI_PREFIX + name);
        }

        HttpSession session = Security.getActualSession(req);
        if (hasAttribute(session.getAttributeNames(), name)) {
            return (String) session.getAttribute(name);
        }

        return null;
    }

    private static Object makeInstance(Class aClass, HttpServletRequest req) throws Exception {
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

    static void processParametersURI(HttpServletRequest req, Endpoint endpoint) {
        if (endpoint.getUrlPattern().contains("{")) {
            addURIParametersToRequest(endpoint.getUrlPattern(), req);
        }
    }

    private static void addURIParametersToRequest(String urlPattern, HttpServletRequest req) {
        String[] patternParts = urlPattern.split("/");
        String[] uriParts = req.getRequestURI().split("/");

        if (patternParts.length != uriParts.length) {
            LOGGER.warn("wrong number of parameters in " + urlPattern);
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

    private static boolean hasAttribute(Enumeration<String> attributes, String name) {
        return Collections.list(attributes)
                .stream().anyMatch(s -> s.equals(name));
    }
}