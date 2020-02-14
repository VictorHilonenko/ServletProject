package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: ready for review
@WebFilter(servletNames = MAIN_PACKAGE + ".web.Servlet")
public class RoutingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        ServletContext context = req.getServletContext();
        Router router = (Router) context.getAttribute(ATTR_ROUTER);

        RequestMethod requestMethod = RequestMethod.valueOf(req.getMethod());

        Role sessionRole = Security.getUserPrincipal(req).getRole();
        String urlPattern = router.getPatternForURI(req.getRequestURI());
        Endpoint endpoint = router.getEndpoint(requestMethod, urlPattern);
        //
        req.setAttribute(ATTR_ENDPOINT, endpoint);

        if (endpoint.hasExceptionForRole(sessionRole)) {
            router.processException(req, resp, endpoint, sessionRole);
        } else if (endpoint.hasRedirectionForRole(sessionRole)) {
            router.processRedirect(req, resp, endpoint, sessionRole);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}