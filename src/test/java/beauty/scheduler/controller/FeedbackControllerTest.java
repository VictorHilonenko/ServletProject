package beauty.scheduler.controller;

import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.FeedbackService;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.form.FeedbackForm;
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

import static beauty.scheduler.util.AppConstants.ATTR_ACTIVE_USERS;
import static beauty.scheduler.util.AppConstants.ATTR_USER_PRINCIPAL;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackControllerTest {

    @InjectMocks
    private FeedbackController instance;

    @Mock
    private ServletContext context;
    @Mock
    private FeedbackService feedbackService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    private UserPrincipal userPrincipal;
    private UserPrincipal adminPrincipal;

    @Before
    public void setUp() {
        when(request.getSession(true)).thenReturn(session);

        userPrincipal = new UserPrincipal(Optional.empty(), "user@mail.com", Role.ROLE_USER, LocaleUtils.getDefaultLocale().getLanguage());
        adminPrincipal = new UserPrincipal(Optional.empty(), "admin@mail.com", Role.ROLE_ADMIN, LocaleUtils.getDefaultLocale().getLanguage());
    }

    @Test
    public void shouldGetFeedbacksToLeaveForUser() throws ExtendedException, SQLException {
        when(feedbackService.getFeedbacksDTOToLeave(userPrincipal)).thenReturn(new ArrayList<>());
        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(userPrincipal);

        instance.feedbacksList(request, new Pagination());

        verify(feedbackService, times(1)).getFeedbacksDTOToLeave(userPrincipal);
    }

    @Test
    public void shouldNotGetFeedbacksToLeaveForUser() throws ExtendedException, SQLException {
        when(feedbackService.getFeedbacksDTOToLeave(adminPrincipal)).thenReturn(new ArrayList<>());
        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(adminPrincipal);

        instance.feedbacksList(request, new Pagination());

        verify(feedbackService, never()).getFeedbacksDTOToLeave(adminPrincipal);
    }

    @Test
    public void shouldGetPagebleFeedbacksListForAll() throws ExtendedException, SQLException {
        Pagination pagination = new Pagination();
        pagination.setPage(0);
        pagination.setPageSize(10);

        when(feedbackService.getFeedbacksDTO(userPrincipal, pagination.getPage(), pagination.getPageSize())).thenReturn(pagination);
        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(userPrincipal);

        instance.feedbacksList(request, pagination);

        verify(feedbackService, times(1)).getFeedbacksDTO(userPrincipal, pagination.getPage(), pagination.getPageSize());
    }

    @Test
    public void shouldLoginUserByQuickLink() throws SQLException, ExtendedException {
        String quickAccessCode = "test";

        User user = new User();
        user.setId(1L);

        when(context.getAttribute(ATTR_ACTIVE_USERS)).thenReturn(new HashSet<>());
        when(request.getServletContext()).thenReturn(context);
        when(feedbackService.getUserByQuickAccessCode(quickAccessCode)).thenReturn(Optional.of(user));

        instance.feedbacksListByQuickLink(quickAccessCode, request);

        verify(session, atLeastOnce()).setAttribute(eq(ATTR_USER_PRINCIPAL), any(UserPrincipal.class));
    }

    @Test
    public void shouldUpdateFeedback() throws SQLException, ExtendedException {
        Feedback feedback = new Feedback();
        Optional<Feedback> optionalFeedback = Optional.of(feedback);

        FeedbackForm feedbackForm = new FeedbackForm();
        feedbackForm.setId("1");
        feedbackForm.setRating("10");
        feedbackForm.setTextMessage("excellent service, thank you!");

        when(feedbackService.getVerifiedFeedback(eq(feedbackForm), any(UserPrincipal.class))).thenReturn(optionalFeedback);
        when(feedbackService.getFeedbacksDTOToLeave(userPrincipal)).thenReturn(new ArrayList<>());
        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(userPrincipal);

        instance.feedbackUpdateAttempt(feedbackForm, request);

        verify(feedbackService, times(1)).updateFeedback(feedback, feedbackForm);
    }
}