package beauty.scheduler.web.myspring.core;

import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.web.myspring.Endpoint;
import beauty.scheduler.web.myspring.annotation.ParamName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Enumeration;

import static beauty.scheduler.util.AppConstants.URI_PREFIX;

public class ParamHelper {

    public static Object[] getAllParameters(Endpoint endpoint, HttpServletRequest req, HttpServletResponse resp) throws NoSuchMethodException, NoSuchFieldException, ExtendedException {
        Method method = endpoint.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

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

    private static boolean hasAttribute(Enumeration<String> attributes, String name) {
        return Collections.list(attributes)
                .stream().anyMatch(s -> s.equals(name));
    }

    private static Object getParameter(Parameter parameter, HttpServletRequest req, HttpServletResponse resp) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, ExtendedException {
        if (parameter.isAnnotationPresent(ParamName.class)) {
            return getAnnotatedParameter(parameter, req, resp);
        } else {
            return getNonAnnotatedParameter(parameter.getParameterizedType(), req, resp);
        }
    }

    private static Object getAnnotatedParameter(Parameter parameter, HttpServletRequest req, HttpServletResponse resp) throws NoSuchFieldException, ExtendedException {
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

    private static Object getNonAnnotatedParameter(Type type, HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
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

    private static String getRequestValue(HttpServletRequest req, String name) throws NoSuchFieldException {
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

    private static Object makeInstance(Class aClass, HttpServletRequest req) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
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
}