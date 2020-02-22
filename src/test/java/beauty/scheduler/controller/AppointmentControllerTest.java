package beauty.scheduler.controller;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.AppointmentService;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController instance;

    @Mock
    private AppointmentService appointmentService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    private UserPrincipal anonymous;

    @Before
    public void setUp() throws Exception {
        when(request.getSession(true)).thenReturn(session);

        anonymous = new UserPrincipal(Optional.empty(), "", Role.ROLE_ANONYMOUS, LocaleUtils.getDefaultLocale().getLanguage());

        when(session.getAttribute(ATTR_USER_PRINCIPAL)).thenReturn(anonymous);
    }

    @Test
    public void shouldReturnErrorWhenIncorrectDates() throws SQLException, ExtendedException {
        LocalDate start = LocalDate.of(2020, 2, 2);
        LocalDate end = LocalDate.of(2020, 2, 1);

        String jsonData = instance.apiAppointmentsListByPeriod(start, end, request);

        assertTrue(jsonData.contains(REST_ERROR));
    }

    @Test
    public void shouldReturnJSONArray() throws SQLException, ExtendedException {
        LocalDate start = LocalDate.of(2020, 2, 1);
        LocalDate end = LocalDate.of(2020, 2, 2);

        when(appointmentService.getAllAppointmentsDTO(start, end, anonymous)).thenReturn(new ArrayList<>());

        String jsonData = instance.apiAppointmentsListByPeriod(start, end, request);

        assertEquals(jsonData, "[]");
    }

    @Test
    public void shouldReturnSuccessStringWhenTimeIsReserved() {
        when(appointmentService.addAppointmentByJSON("test", anonymous)).thenReturn(REST_SUCCESS);

        String message = instance.apiAppointmentCreateAttempt("test", request);

        assertTrue(message.contains(REST_SUCCESS));
    }

    @Test
    public void shouldReturnSuccessStringWhenServiceProvided() {
        when(appointmentService.setServiceProvidedByJSON("test", anonymous)).thenReturn(REST_SUCCESS);

        String message = instance.apiSetServiceProvidedAttempt("test", request);

        assertTrue(message.contains(REST_SUCCESS));
    }
}