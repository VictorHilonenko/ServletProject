package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.ContentType;
import beauty.scheduler.web.myspring.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static beauty.scheduler.util.AppConstants.*;
import static beauty.scheduler.web.myspring.core.ParamHelper.getAllParameters;

public class ProcessHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessHelper.class);

    static String process(Router router, HttpServletRequest req, HttpServletResponse resp) {
        Endpoint endpoint = (Endpoint) req.getAttribute(ATTR_ENDPOINT);
        resp.setContentType(endpoint.getContentType().getStrContentType());

        String result = processController(router, req, resp, endpoint);

        if (ContentType.HTML.equals(endpoint.getContentType())) {

            return processResultHTML(result, req, resp, endpoint);

        } else if (ContentType.JSON.equals(endpoint.getContentType())) {

            return processResultJSON(result, resp);

        } else {

            processException(req, resp, ExceptionKind.NOT_SUPPORTED);
            return "exception"; //for debug purposes

        }
    }

    private static String processController(Router router, HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint) {
        Method method = endpoint.getMethod();

        if (method == null) { //it's almost impossible, but for reliability, lets's do this check
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return "exception";
        }

        ParamHelper.processParametersURI(req, endpoint);

        Object[] args = getAllParameters(endpoint, req, resp);

        Object classInstance = router.getBean(method.getDeclaringClass().getSimpleName());

        try {
            return (String) method.invoke(classInstance, args);
        } catch (Exception e) {
            processException(req, resp, e);
            return "exception";
        }
    }

    private static String processResultHTML(String resultPage, HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint) {
        if (StringUtils.isEmpty(resultPage)) {
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

        try {
            req.getRequestDispatcher(resultPage).forward(req, resp);
        } catch (Exception e) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
        }

        return resultPage;
    }

    private static String processResultJSON(String jsonData, HttpServletResponse resp) {
        PrintWriter out;

        try {
            out = resp.getWriter();
        } catch (IOException e) {
            return REST_ERROR;
        }

        out.print(jsonData);
        out.flush();

        return REST_SUCCESS;
    }

    private static void processRedirect(HttpServletRequest req, HttpServletResponse resp, String redirectTo) {
        try {
            resp.sendRedirect(redirectTo);
        } catch (IOException e) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
        }
    }

    static void processRedirect(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
        String redirectTo = endpoint.getRedirectionForRole(role);

        if (StringUtils.isEmpty(redirectTo)) {
            processException(req, resp, ExceptionKind.PAGE_NOT_FOUND);
            return;
        }

        processRedirect(req, resp, redirectTo);
    }

    private static void processException(HttpServletRequest req, HttpServletResponse resp, ExceptionKind exceptionKind) {
        if (exceptionKind == null) {
            exceptionKind = ExceptionKind.PAGE_NOT_FOUND;
        }

        if (exceptionKind.equals(ExceptionKind.ACCESS_DENIED)) {
            Security.logOut(req);
        }

        try {
            req.setAttribute("exception", exceptionKind);
            resp.sendError(exceptionKind.getStatusCode(), exceptionKind.getErrorMessage());
        } catch (IOException e) {
            LOGGER.error("Curious: exceptionn during sending an error");
        }
    }

    static void processException(HttpServletRequest req, HttpServletResponse resp, Endpoint endpoint, Role role) {
        processException(req, resp, endpoint.getExceptionForRole(role));
    }

    private static void processException(HttpServletRequest req, HttpServletResponse resp, Exception exception) {
        if (exception.getClass().equals(ExtendedException.class)) {
            processException(req, resp, ((ExtendedException) exception).getExceptionKind());
        } else if (exception instanceof InvocationTargetException) {
            processException(req, resp, (Exception) ((InvocationTargetException) exception).getTargetException());
        } else {
            processException(req, resp, ExceptionKind.SOME_ERROR);
        }
    }
}