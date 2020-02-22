package beauty.scheduler.controller;

import beauty.scheduler.dto.AppointmentDTO;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.AppointmentService;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.ContentType;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.*;
import beauty.scheduler.web.myspring.core.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static beauty.scheduler.util.AppConstants.REST_ERROR;

@ServiceComponent
public class AppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);

    @InjectDependency
    private AppointmentService appointmentService;

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/api/appointments/{start}/{end}", contentType = ContentType.JSON)
    public String apiAppointmentsListByPeriod(@ParamName(name = "start") LocalDate start,
                                              @ParamName(name = "end") LocalDate end, HttpServletRequest req) throws SQLException, ExtendedException {

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        if (start.isAfter(end)) {
            return StringUtils.toJSON(REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang()));
        }

        List<AppointmentDTO> list = appointmentService.getAllAppointmentsDTO(start, end, userPrincipal);

        return StringUtils.toJSON(list);
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/api/appointments", contentType = ContentType.JSON)
    @Restriction(role = Role.notAuthenticated, exception = ExceptionKind.ACCESS_DENIED)
    @Restriction(role = Role.staff, exception = ExceptionKind.ACCESS_DENIED)
    public String apiAppointmentCreateAttempt(@Json String jsonData, HttpServletRequest req) {
        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String message = appointmentService.addAppointmentByJSON(jsonData, userPrincipal);

        return StringUtils.toJSON(message);
    }

    @EndpointMethod(requestMethod = RequestMethod.PUT, urlPattern = "/api/appointments", contentType = ContentType.JSON)
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.ACCESS_DENIED)
    @Restriction(role = Role.ROLE_ADMIN, exception = ExceptionKind.ACCESS_DENIED)
    public String apiSetServiceProvidedAttempt(@Json String jsonData, HttpServletRequest req) {
        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String message = appointmentService.setServiceProvidedByJSON(jsonData, userPrincipal);

        return StringUtils.toJSON(message);
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}