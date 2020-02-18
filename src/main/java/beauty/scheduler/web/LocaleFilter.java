package beauty.scheduler.web;

import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.core.Security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.ATTR_LANG;
import static beauty.scheduler.util.AppConstants.MAIN_PACKAGE;

@WebFilter(servletNames = MAIN_PACKAGE + ".web.Servlet")
public class LocaleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        HttpServletRequest req = (HttpServletRequest) request;

        HttpSession session = Security.getActualSession(req);
        String sessionLang = (String) session.getAttribute(ATTR_LANG);

        String requestLang = Optional.ofNullable(req.getParameter(ATTR_LANG)).orElse("").toLowerCase();

        if (!LocaleUtils.getLangIsEnabled(requestLang)) {
            requestLang = "";
        }

        String newCorrectLang;
        if (!"".equals(requestLang)) {
            newCorrectLang = requestLang;

            if (!sessionLang.equals(newCorrectLang)) {
                session.setAttribute(ATTR_LANG, newCorrectLang);
                UserPrincipal userPrincipal = Security.getUserPrincipal(session);
                userPrincipal.setCurrentLang(newCorrectLang);
            }
        } else {
            newCorrectLang = sessionLang;
        }

        req.setAttribute(ATTR_LANG, newCorrectLang);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}