package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Set;

import static beauty.scheduler.util.AppConstants.*;

public class Security {
    private static final Logger LOGGER = LoggerFactory.getLogger(Security.class);


    private static void setAnonimousPrincipal(HttpSession session) {
        String sessionLang = (String) session.getAttribute(ATTR_LANG);
        session.setAttribute(ATTR_USER_PRINCIPAL, new UserPrincipal(Optional.empty(), "", Role.ROLE_ANONYMOUS, sessionLang));
    }

    public static void makeSessionInitialized(HttpSession session) {
        String sessionLang = (String) session.getAttribute(ATTR_LANG);
        if (!Optional.ofNullable(sessionLang).isPresent()) {
            session.setAttribute(ATTR_LANG, LocaleUtils.getDefaultLocale().getLanguage());
        }

        UserPrincipal userPrincipal = getUserPrincipal(session);
        if (!Optional.ofNullable(userPrincipal).isPresent()) {
            setAnonimousPrincipal(session);
        }
    }

    public static HttpSession getActualSession(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        makeSessionInitialized(session);

        return session;
    }

    public static UserPrincipal getUserPrincipal(HttpSession session) {
        return (UserPrincipal) session.getAttribute(ATTR_USER_PRINCIPAL);
    }

    public static UserPrincipal getUserPrincipal(HttpServletRequest req) {
        return getUserPrincipal(getActualSession(req));
    }

    public static void logInUser(HttpServletRequest req, User user) {
        ServletContext context = req.getServletContext();
        HttpSession session = getActualSession(req);
        Set<UserPrincipal> activeUsers = (Set<UserPrincipal>) context.getAttribute(ATTR_ACTIVE_USERS);

        String sessionLang = Optional.ofNullable((String) session.getAttribute(ATTR_LANG)).orElse(LocaleUtils.getDefaultLocale().getLanguage());
        UserPrincipal userPrincipal = new UserPrincipal(Optional.of(user.getId()), user.getEmail(), user.getRole(), sessionLang);

        activeUsers.add(userPrincipal);
        session.setAttribute(ATTR_USER_PRINCIPAL, userPrincipal);
    }

    public static void logOut(HttpSession session) {
        makeSessionInitialized(session);

        ServletContext context = session.getServletContext();
        Set<UserPrincipal> activeUsers = (Set<UserPrincipal>) context.getAttribute(ATTR_ACTIVE_USERS);

        UserPrincipal userPrincipal = getUserPrincipal(session);

        activeUsers.remove(userPrincipal);
        setAnonimousPrincipal(session);
    }

    public static void logOut(HttpServletRequest req) {
        logOut(getActualSession(req));
    }
}