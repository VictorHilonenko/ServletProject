package beauty.scheduler.web.myspring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static beauty.scheduler.util.AppConstants.ATTR_ROUTER;
import static beauty.scheduler.util.AppConstants.MAIN_PACKAGE;

//NOTE: ready for review
@WebFilter(servletNames = MAIN_PACKAGE + ".web.Servlet")
public class RoutingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        ServletContext context = req.getServletContext();
        Router router = (Router) context.getAttribute(ATTR_ROUTER);

        if (!router.restricted(req, resp)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}