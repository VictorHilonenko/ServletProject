package beauty.scheduler.controller;

import beauty.scheduler.dto.AppointmentDTO;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.AppointmentService;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.*;
import beauty.scheduler.web.myspring.core.Router;
import beauty.scheduler.web.myspring.core.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ServiceComponent
public class AppointmentsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentsController.class);

    @InjectDependency
    private AppointmentService appointmentService;

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/api/appointments/{start}/{end}")
    public String apiAppointmentsListByPeriod(@ParamName(name = "start") LocalDate start, @ParamName(name = "end") LocalDate end,
                                              HttpServletRequest req, HttpServletResponse resp) throws SQLException, ExtendedException {
        if (start.isAfter(end)) {
            throw new ExtendedException(ExceptionKind.WRONG_DATA_PASSED);
        }

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        List<AppointmentDTO> list = appointmentService.getAllAppointmentsDTO(start, end, userPrincipal);

        return Router.sendRESTData(list, resp);
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/api/appointments")
    @Restriction(role = Role.notAuthenticated, exception = ExceptionKind.ACCESS_DENIED)
    @Restriction(role = Role.staff, exception = ExceptionKind.ACCESS_DENIED)
    public String apiAppointmentCreateAttempt(HttpServletRequest req, HttpServletResponse resp) throws IOException, ExtendedException {
        String jsonData = req.getReader()
                .lines()
                .collect(Collectors.joining());

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String message = appointmentService.addAppointmentByJSON(jsonData, userPrincipal);

        return Router.sendRESTData(message, resp);
    }

    @EndpointMethod(requestMethod = RequestMethod.PUT, urlPattern = "/api/appointments")
    @Restriction(role = Role.nonstaff, exception = ExceptionKind.ACCESS_DENIED)
    @Restriction(role = Role.ROLE_ADMIN, exception = ExceptionKind.ACCESS_DENIED)
    public String apiSetServiceProvidedAttempt(HttpServletRequest req, HttpServletResponse resp) throws IOException, ExtendedException {
        String jsonData = req.getReader()
                .lines()
                .collect(Collectors.joining());

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        String message = appointmentService.setServiceProvidedByJSON(jsonData, userPrincipal);

        return Router.sendRESTData(message, resp);
    }

    public AppointmentService getAppointmentService() {
        return appointmentService;
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}