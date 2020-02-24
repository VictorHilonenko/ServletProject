package beauty.scheduler.service;

import beauty.scheduler.dao.FeedbackDao;
import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.dto.FeedbackDTO;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.DEFAULT_PAGE_SIZE;
import static beauty.scheduler.util.AppConstants.MAIL_USERNAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService instance;

    @Mock
    private FeedbackDao feedbackDao;
    @Mock
    private UserService userService;
    @Mock
    private EmailMessageService emailMessageService;

    private UserPrincipal userPrincipal;
    private Feedback feedbackSample;
    private FeedbackForm formSample;

    @Before
    public void setUp() {
        userPrincipal = new UserPrincipal(Optional.of(1), MAIL_USERNAME, Role.ROLE_USER, LocaleUtils.getDefaultLocale().getLanguage());

        User user = new User(1, "name", "", "", "", "", "", "", Role.ROLE_USER, ServiceType.NULL);

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setCustomer(user);
        appointment.setMaster(user);
        appointment.setServiceType(ServiceType.HAIRDRESSING);

        feedbackSample = new Feedback();
        feedbackSample.setAppointment(appointment);
        feedbackSample.setTextMessage("");

        formSample = new FeedbackForm();
        formSample.setId("1");
        formSample.setRating("10");
        formSample.setTextMessage("the best service");
    }

    @Test
    public void getFeedbacksDTOToLeave() throws ExtendedException, SQLException {
        List<Feedback> list = new ArrayList<>();
        list.add(feedbackSample);

        when(feedbackDao.getFeedbacksToLeave(anyInt())).thenReturn(list);

        List<FeedbackDTO> listFeedbackDTO = instance.getFeedbacksDTOToLeave(userPrincipal);

        assertEquals(listFeedbackDTO.size(), listFeedbackDTO.size());
    }

    @Test
    public void shouldReturnPageFeedbacksDTO() throws SQLException, ExtendedException {
        List<Feedback> list = new ArrayList<>();
        list.add(feedbackSample);

        Pagination<Feedback> paginationFeedback = new Pagination<>(0, DEFAULT_PAGE_SIZE, 1, list);

        when(feedbackDao.getAllForUser(userPrincipal.getId().get(), 0, DEFAULT_PAGE_SIZE)).thenReturn(paginationFeedback);

        Pagination<FeedbackDTO> paginationFeedbackDTO = instance.getFeedbacksDTO(userPrincipal, 0, DEFAULT_PAGE_SIZE);

        assertEquals(paginationFeedback.getTotalRecords(), paginationFeedbackDTO.getTotalRecords());
    }

    @Test
    public void shouldGetUserByQuickAccessCode() throws SQLException, ExtendedException {
        when(emailMessageService.getEmailByQuickAccessCode(anyString())).thenReturn(MAIL_USERNAME);

        instance.getUserByQuickAccessCode("test");

        verify(userService, times(1)).findByEmail(MAIL_USERNAME);
    }

    @Test
    public void shouldGetVerifiedFeedback() throws SQLException, ExtendedException {
        Optional<Feedback> optionalFeedbackExpect = Optional.of(feedbackSample);

        when(feedbackDao.getById(anyInt())).thenReturn(optionalFeedbackExpect);

        Optional<Feedback> optionalFeedbackActual = instance.getVerifiedFeedback(formSample, userPrincipal);

        assertEquals(optionalFeedbackActual, optionalFeedbackExpect);
    }

    @Test
    public void updateFeedback() throws SQLException, ExtendedException {
        instance.updateFeedback(feedbackSample, formSample);

        verify(feedbackDao, times(1)).update(feedbackSample);
    }
}