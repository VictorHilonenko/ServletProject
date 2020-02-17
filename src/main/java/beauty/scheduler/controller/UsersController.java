package beauty.scheduler.controller;

import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.service.UserService;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.form.FormValidator;
import beauty.scheduler.web.form.LoginForm;
import beauty.scheduler.web.form.RegistrationForm;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.Router;
import beauty.scheduler.web.myspring.Security;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: ready for review
@ServiceComponent
public class UsersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    @InjectDependency
    private UserService userService;

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/registration")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/registration.jsp")
    public String registrationPage() {
        LOGGER.info("registrationPage is shown");
        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/registration")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/registration.jsp")
    public String registrationAttempt(RegistrationForm form, HttpServletRequest req) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, InstantiationException, ExtendedException {

        Map<String, String> errors = new HashMap<>();
        FormValidator.validate(form, errors);

        if (userService.exists("email", form.getEmail())) {
            FormValidator.appendError(errors, "email", "warning.emailIsTaken");
        }

        if (errors.size() > 0) {
            form.setPassword(""); //classical way
            req.setAttribute(ATTR_FORM, form);

            req.setAttribute(ATTR_ERRORS, errors);

            return DEFAULT_TEMPLATE;
        }

        userService.createUser(form);

        return "/reg_success.jsp";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/login")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/login.jsp")
    public String loginPage() {
        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/login")
    @Restriction(role = Role.authenticated, redirection = "/logout")
    @DefaultTemplate(template = "/login.jsp")
    public String loginAttempt(LoginForm form, HttpServletRequest req) throws SQLException, ExtendedException {

        Map<String, String> errors = new HashMap<>();
        FormValidator.validate(form, errors);

        Optional<User> verifiedUser = userService.getVerifiedUser(form.getEmail(), form.getPassword());

        if (!verifiedUser.isPresent()) {
            FormValidator.appendError(errors, "", "warning.invalidUsernameAndOrPassword");
        }
        if (errors.size() > 0) {
            form.setPassword(""); //classical way

            req.setAttribute(ATTR_FORM, form);
            req.setAttribute(ATTR_ERRORS, errors);

            return DEFAULT_TEMPLATE;
        }

        //depending on business logic we could call userService, so it could make
        //some actions like saving login/logout timing to DB, etc. and then call Security.loginUser
        //but in this project task that was not determined, so, let's "Keep It Simple Stupid":
        Security.logInUser(req, verifiedUser.get());

        return REDIRECT + "/";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/logout")
    @Restriction(role = Role.notAuthenticated, redirection = "/")
    public String logout(HttpServletRequest req, HttpServletResponse resp) {

        //depending on business logic we could call userService, so it could make
        //some actions like saving login/logout timing to DB, etc. and then call Security.loginUser
        //but in this project task that was not determined, so, let's "Keep It Simple Stupid":
        Security.logOut(req);

        return REDIRECT + "/";
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/admin")
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.PAGE_NOT_FOUND)
    @Restriction(role = Role.ROLE_MASTER, exception = ExceptionKind.ACCESS_DENIED)
    @DefaultTemplate(role = Role.ROLE_ADMIN, template = "/admin.jsp")
    public String apiUserListForAdmin(HttpServletRequest req, HttpServletResponse resp) throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ExtendedException {

        req.setAttribute(ATTR_USERS, userService.getAllUserDTO());
        req.setAttribute(ATTR_ROLES, Role.getAllByTag("front"));
        req.setAttribute(ATTR_SERVICE_TYPES, ServiceType.values());

        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.PUT, urlPattern = "/api/users")
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.PAGE_NOT_FOUND)
    @Restriction(role = Role.ROLE_MASTER, exception = ExceptionKind.ACCESS_DENIED)
    public String apiUserUpdateAttempt(HttpServletRequest req, HttpServletResponse resp) throws IOException, ExtendedException {
        String jsonData = req.getReader()
                .lines()
                .collect(Collectors.joining());

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String status = userService.updateUserByJSON(jsonData, userPrincipal);

        return Router.sendRESTData(status, resp);
    }

    public UserService getUserService() {
        return this.userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}