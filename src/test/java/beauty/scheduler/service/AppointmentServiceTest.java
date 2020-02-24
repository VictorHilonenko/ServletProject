package beauty.scheduler.service;

import beauty.scheduler.dao.AppointmentDao;
import beauty.scheduler.dao.EmailMessageDao;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
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

import static beauty.scheduler.util.AppConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService instance;

    @Mock
    private AppointmentDao appointmentDao;
    @Mock
    private EmailMessageService emailMessageService;
    @Mock
    private EmailMessageDao emailMessageDao;

    private UserPrincipal userPrincipal;
    private Appointment appointmentSample;

    @Before
    public void setUp() {
        userPrincipal = new UserPrincipal(Optional.of(1), MAIL_USERNAME, Role.ROLE_MASTER, LocaleUtils.getDefaultLocale().getLanguage());

        User user = new User(1, "name", "", "", "", "", "", "", Role.ROLE_MASTER, ServiceType.NULL);

        appointmentSample = new Appointment();
        appointmentSample.setAppointmentDate(LocalDate.now());
        appointmentSample.setCustomer(user);
        appointmentSample.setMaster(user);
        appointmentSample.setServiceType(ServiceType.HAIRDRESSING);
    }

    @Test
    public void shouldCallFindByPeriod() throws SQLException, ExtendedException {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(1);

        List<Appointment> list = new ArrayList<>();
        list.add(appointmentSample);

        when(appointmentDao.findByPeriod(start, end)).thenReturn(list);

        userPrincipal.setRole(Role.ROLE_USER);
        instance.getAllAppointmentsDTO(start, end, userPrincipal);
        userPrincipal.setRole(Role.ROLE_MASTER);
        instance.getAllAppointmentsDTO(start, end, userPrincipal);
        userPrincipal.setRole(Role.ROLE_ADMIN);
        instance.getAllAppointmentsDTO(start, end, userPrincipal);

        verify(appointmentDao, times(3)).findByPeriod(start, end);
    }

    @Test
    public void shouldCallReserveTimeOnCorrectDataWhenAddAppointmentByJSON() {
        String jsonData = "{\"date\":\"2020-02-01\",\"time\":\"15\",\"serviceType\":\"HAIRDRESSING\"}";

        when(appointmentDao.reserveTime(1, "2020-02-01", "15", ServiceType.HAIRDRESSING.toString(), LocaleUtils.LOCALE_ENGLISH.getLanguage())).thenReturn("");

        instance.addAppointmentByJSON(jsonData, userPrincipal);

        verify(appointmentDao, times(1)).reserveTime(1, "2020-02-01", "15", ServiceType.HAIRDRESSING.toString(), LocaleUtils.LOCALE_ENGLISH.getLanguage());
    }

    @Test
    public void shouldReturnErrorOnIncorrectDataWhenAddAppointmentByJSON() {
        String jsonData = "{\"date\":\"2020-02-01\",\"time\":\"2\",\"serviceType\":\"HAIRDRESSING\"}";

        String message = instance.addAppointmentByJSON(jsonData, userPrincipal);

        assertTrue(message.contains(REST_ERROR));
    }

    @Test
    public void shouldReturnErrorOnIncorrectDataWhenSetServiceProvided() throws SQLException, ExtendedException {
        String jsonData = "{\"id\":\"error\",\"serviceProvided\":\"true\"}";

        String message = instance.setServiceProvidedByJSON(jsonData, userPrincipal, false);

        assertTrue(message.contains(REST_ERROR));
    }

    @Test
    public void shouldSetServiceProvided() throws SQLException, ExtendedException {
        String jsonData = "{\"id\":\"1\",\"serviceProvided\":\"true\"}";

        when(appointmentDao.getById(1)).thenReturn(Optional.of(appointmentSample));

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(MAIL_USERNAME);

        when(emailMessageService.createEmailForProvidedService(appointmentSample)).thenReturn(emailMessage);

        when(appointmentDao.update(appointmentSample)).thenReturn(true);
        when(emailMessageDao.create(emailMessage)).thenReturn(true);

        String message = instance.setServiceProvidedByJSON(jsonData, userPrincipal, false);

        verify(emailMessageService, times(1)).pushEmailSending(true);

        assertEquals(message, REST_SUCCESS);
    }
}