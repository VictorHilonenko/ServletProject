package beauty.scheduler.controller;

import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.service.UserService;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.*;
import beauty.scheduler.web.myspring.core.FormValidator;
import beauty.scheduler.web.myspring.core.Security;
import beauty.scheduler.web.myspring.enums.ContentType;
import beauty.scheduler.web.myspring.enums.RequestMethod;
import beauty.scheduler.web.myspring.form.LoginForm;
import beauty.scheduler.web.myspring.form.RegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.*;

@ServiceComponent
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @InjectDependency
    private UserService userService;

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/registration")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/WEB-INF/registration.jsp")
    public String registrationPage() {
        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/registration")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/WEB-INF/registration.jsp")
    public String registrationAttempt(RegistrationForm form, HttpServletRequest req) throws Exception {

        Map<String, String> errors = new HashMap<>();
        FormValidator.validate(form, errors);

        if (userService.exists("email", form.getEmail())) {
            FormValidator.appendError(errors, "email", "warning.emailIsTaken");
        }

        if (errors.size() > 0) {
            form.setPassword("");
            req.setAttribute(ATTR_FORM, form);

            req.setAttribute(ATTR_ERRORS, errors);

            return DEFAULT_TEMPLATE;
        }

        userService.createUser(form);

        return "/WEB-INF/reg_success.jsp";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/login")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/WEB-INF/login.jsp")
    public String loginPage() {
        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/login")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/WEB-INF/login.jsp")
    public String loginAttempt(LoginForm form, HttpServletRequest req) throws SQLException, ExtendedException {

        Map<String, String> errors = new HashMap<>();
        FormValidator.validate(form, errors);

        Optional<User> verifiedUser = userService.getVerifiedUser(form.getEmail(), form.getPassword());

        if (!verifiedUser.isPresent()) {
            FormValidator.appendError(errors, "", "warning.invalidUsernameAndOrPassword");
        }
        if (errors.size() > 0) {
            form.setPassword("");

            req.setAttribute(ATTR_FORM, form);
            req.setAttribute(ATTR_ERRORS, errors);

            return DEFAULT_TEMPLATE;
        }

        UserPrincipal userPrincipal = Security.buildUserPrincipal(req, verifiedUser.get());
        Security.logInUser(req, userPrincipal);

        return REDIRECT + "/";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/logout")
    @Restriction(role = Role.notAuthenticated, redirection = "/")
    public String logout(HttpServletRequest req) {

        Security.logOut(req);

        return REDIRECT + "/";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/admin")
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.PAGE_NOT_FOUND)
    @Restriction(role = Role.ROLE_MASTER, exception = ExceptionKind.ACCESS_DENIED)
    @DefaultTemplate(role = Role.ROLE_ADMIN, template = "/WEB-INF/admin.jsp")
    public String apiUserListForAdmin(HttpServletRequest req) throws Exception {

        req.setAttribute(ATTR_USERS, userService.getAllUserDTO());
        req.setAttribute(ATTR_ROLES, Role.getAllByTag("front"));
        req.setAttribute(ATTR_SERVICE_TYPES, ServiceType.values());

        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.PUT, urlPattern = "/api/users", contentType = ContentType.JSON)
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.PAGE_NOT_FOUND)
    @Restriction(role = Role.ROLE_MASTER, exception = ExceptionKind.ACCESS_DENIED)
    public String apiUserUpdateAttempt(@Json String jsonData, HttpServletRequest req) {
        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String message = userService.updateUserByJSON(jsonData, userPrincipal);

        return StringUtils.toJSON(message);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}