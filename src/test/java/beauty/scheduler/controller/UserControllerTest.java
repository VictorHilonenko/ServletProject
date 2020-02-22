package beauty.scheduler.controller;

import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.UserService;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.form.LoginForm;
import beauty.scheduler.web.myspring.form.RegistrationForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController instance;

    @Mock
    private ServletContext context;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        when(request.getSession(true)).thenReturn(session);

        userPrincipal = new UserPrincipal(Optional.empty(), "user@mail.com", Role.ROLE_USER, LocaleUtils.getDefaultLocale().getLanguage());
    }

    @Test
    public void shouldReturnDefaultForRegistration() {
        assertEquals(instance.registrationPage(), DEFAULT_TEMPLATE);
    }

    @Test
    public void shouldCreateUser() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setFirstNameEn("FirstNameEn");
        registrationForm.setLastNameEn("LastNameEn");
        registrationForm.setFirstNameUk("Ім'я");
        registrationForm.setLastNameUk("Прізвище");
        registrationForm.setEmail("test@mail.com");
        registrationForm.setTelNumber("+380671234567");
        registrationForm.setPassword("Password");

        when(userService.exists(eq("email"), anyString())).thenReturn(false);

        instance.registrationAttempt(registrationForm, request);

        verify(userService, times(1)).createUser(registrationForm);
    }

    @Test
    public void shouldReturnDefaultForLogin() {
        assertEquals(instance.loginPage(), DEFAULT_TEMPLATE);
    }

    @Test
    public void shouldLoginUser() throws SQLException, ExtendedException {
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail("test@mail.com");
        loginForm.setPassword("Password");

        User user = new User();
        Optional<User> optionalUser = Optional.of(user);

        when(context.getAttribute(ATTR_ACTIVE_USERS)).thenReturn(new HashSet<>());
        when(request.getServletContext()).thenReturn(context);
        when(userService.getVerifiedUser(loginForm.getEmail(), loginForm.getPassword())).thenReturn(optionalUser);

        instance.loginAttempt(loginForm, request);

        verify(session, atLeastOnce()).setAttribute(eq(ATTR_USER_PRINCIPAL), any(UserPrincipal.class));
    }

    @Test
    public void shouldLogoutUser() {
        when(context.getAttribute(ATTR_ACTIVE_USERS)).thenReturn(new HashSet<>());
        when(session.getServletContext()).thenReturn(context);

        instance.logout(request);

        verify(session, atLeastOnce()).setAttribute(eq(ATTR_USER_PRINCIPAL), any(UserPrincipal.class));
    }

    @Test
    public void shouldSetUserList() throws Exception {
        when(userService.getAllUserDTO()).thenReturn(new ArrayList<>());

        instance.apiUserListForAdmin(request);

        verify(request, times(1)).setAttribute(eq(ATTR_USERS), anyList());
        verify(request, times(1)).setAttribute(eq(ATTR_ROLES), anyList());
        verify(request, times(1)).setAttribute(eq(ATTR_SERVICE_TYPES), anyList());
    }

    @Test
    public void shouldReturnSuccessStringWhenSaved() {
        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(userPrincipal);
        when(userService.updateUserByJSON("test", userPrincipal)).thenReturn(REST_SUCCESS);

        String message = instance.apiUserUpdateAttempt("test", request);

        assertTrue(message.contains(REST_SUCCESS));
    }
}