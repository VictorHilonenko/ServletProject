package beauty.scheduler.service;

import beauty.scheduler.dao.AppointmentDao;
import beauty.scheduler.dto.AppointmentDTO;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotations.InjectDependency;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: partly ready for review
@ServiceComponent
public class AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentService.class);

    @InjectDependency
    private AppointmentDao appointmentDao;
    @InjectDependency
    private UserService userService;
    @InjectDependency
    private EmailMessageService emailMessageService;

    public AppointmentService() {
    }

    public List<AppointmentDTO> getAllAppointmentsDTO(LocalDate start, LocalDate end, UserPrincipal userPrincipal) throws SQLException, ExtendedException {
        return appointmentDao.findByPeriod(start, end)
                .stream()
                .map(a -> appointmentToDTO(a, userPrincipal))
                .collect(Collectors.toList());
    }

    public String addAppointmentByJSON(String jsonData, UserPrincipal userPrincipal) {
        Map<String, String> map = new Gson().fromJson(jsonData, Map.class);
        String strDate = map.getOrDefault("date", "");
        String strTime = map.getOrDefault("time", "");
        String strServiceType = map.getOrDefault("serviceType", "");

        //validate incoming data
        String message = validateAppointmentDataForAdd(strDate, strTime, strServiceType);

        if (!"".equals(message)) {
            //not valid
            return message;
        }

        //try to reserve Master for the date and time, if any is available for that ServiceType
        message = appointmentDao.reserveTime(userPrincipal.getId().get(), strDate, strTime, strServiceType);

        if (!"".equals(message)) {
            //db issue or no idle master
            return message;
        }

        return REST_SUCCESS;
    }

    public String validateAppointmentDataForAdd(String strDate, String strTime, String strServiceType) {
        strTime = (strTime.length() == 1 ? "0" : "") + strTime;
        String strDateTime = strDate + "T" + strTime + ":00:00.000";

        //check format
        Byte time;
        LocalDateTime reserveDateTime;
        try {
            time = Byte.parseByte(strTime);
            reserveDateTime = LocalDateTime.parse(strDateTime);
        } catch (DateTimeParseException e) {
            LOGGER.error("wrong data format of " + strDateTime);
            return "error:wrong data format"; //(!!!) resource bundle
        }

        //check past time
        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        if (reserveDateTime.isBefore(nowDateTime)) {
            return "error:date in the past"; //(!!!) resource bundle
        }

        //check work time
        if ((time < WORK_TIME_STARTS) || (time > WORK_TIME_ENDS)) {
            LOGGER.error("not working time " + strDateTime);
            return "error:not working time"; //(!!!) resource bundle
        }

        //check service type
        try {
            strServiceType = ServiceType.valueOf(strServiceType).name();
        } catch (IllegalArgumentException e) {
            LOGGER.error("wrong service type " + strServiceType);
            return "error:wrong service type"; //(!!!) resource bundle
        }

        return ""; //if OK
    }

    public String setServiceProvidedByJSON(String jsonData, UserPrincipal userPrincipal) {
        Map<String, String> map = new Gson().fromJson(jsonData, Map.class);
        String strId = map.getOrDefault("id", "");
        String strServiceProvided = map.getOrDefault("serviceProvided", "");

        //validate incoming data
        String message = validateIncomingDataForServiceProvided(strId, strServiceProvided);

        if (!"".equals(message)) {
            //not valid
            return message;
        }

        //try to find the Appointment
        Optional<Appointment> optionalAppointment;
        try {
            optionalAppointment = appointmentDao.getById(Long.parseLong(strId));
        } catch (SQLException | ExtendedException e) {
            return "error:REPOSITORY_ISSUE"; //(!!!) hardcode "error" to const
        }

        if (!optionalAppointment.isPresent()) {
            return "error:appointment not found"; //(!!!) hardcode "error" to const
        }

        Appointment appointment = optionalAppointment.get();

        //check politics for set "service provided"
        message = validateAppointmentForSetServiceProvided(appointment, userPrincipal);

        if (!"".equals(message)) {
            //not valid
            return message;
        }

        //action itself
        appointment.setServiceProvided(true);

        boolean updated = false;
        try {
            updated = appointmentDao.update(appointment);
        } catch (SQLException | ExtendedException e) {
            LOGGER.error("SQLException updateServiceProvidedByJSON");
            return "error:REPOSITORY_ISSUE"; //(!!!) hardcode "error" to const
        }

        if (!updated) {
            return "error:REPOSITORY_ISSUE"; //(!!!) hardcode "error" to const
        }

        emailMessageService.sendRequestForFeedbackToCustomer(appointment);

        return REST_SUCCESS;
    }

    private String validateAppointmentForSetServiceProvided(Appointment appointment, UserPrincipal userPrincipal) {
        //is it not set yet?
        if (appointment.getServiceProvided()) {
            return "error:already set"; //(!!!) resource bundle
        }

        //maybe it is in future?
        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), LocalTime.of(appointment.getAppointmentTime(), 0));

        if (appointmentDateTime.isAfter(nowDateTime)) {
            return "error:date is in future"; //(!!!) resource bundle
        }

        //is user authenticated?
        if (!userPrincipal.getId().isPresent()) {
            return "error:not authenticated"; //(!!!) resource bundle
        }

        //does he has authority?
        if (!userPrincipal.getRole().equals(Role.ROLE_MASTER)) {
            return "error:has no authority"; //(!!!) resource bundle
        }

        //is it his/her appointment?
        if (!appointment.getMaster().getId().equals(userPrincipal.getId().get())) {
            return "error:only master can set this"; //(!!!) resource bundle
        }

        return ""; //if OK
    }

    public String validateIncomingDataForServiceProvided(String strId, String strServiceProvided) {
        //check format
        try {
            Long id = Long.parseLong(strId);
        } catch (DateTimeParseException e) {
            LOGGER.error("wrong data format of " + strId);
            return "error:wrong data format"; //(!!!) resource bundle
        }

        if (!Boolean.parseBoolean(strServiceProvided)) {
            LOGGER.info("attempt to set service provided unchecked");
            return "error:you can only set it checked"; //(!!!) resource bundle
        }

        return ""; //if OK
    }

    public AppointmentDTO appointmentToDTO(Appointment appointment, UserPrincipal userPrincipal) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        setFieldsAndRightsToDTOAccordingToPolicy(appointmentDTO, appointment, userPrincipal);

        return appointmentDTO;
    }

    //fields with prefixes "rights_" are used in frontend to provide rights for separate roles
    //values can be: "H" - hidden, "R" - read only, "W" - write
    private void setFieldsAndRightsToDTOAccordingToPolicy(AppointmentDTO appointmentDTO, Appointment appointment, UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        Role role = userPrincipal.getRole();
        String lang = userPrincipal.getCurrentLang();
        HashMap<String, String> fieldsMap = appointmentDTO.getMap();

        //common fields:
        fieldsMap.put("id", appointment.getId().toString());
        fieldsMap.put("rights_id", "H");
        fieldsMap.put("date", appointment.getAppointmentDate().toString());
        fieldsMap.put("rights_date", "R");
        fieldsMap.put("time", appointment.getAppointmentTime().toString());
        fieldsMap.put("rights_time", "R");
        fieldsMap.put("serviceType", appointment.getServiceType().name());
        fieldsMap.put("rights_serviceType", "R");
        //

        if (role.equals(Role.ROLE_USER)) {
            addFieldsForUser(fieldsMap, appointment, email, lang);
        } else if (role.equals(Role.ROLE_MASTER)) {
            addFieldsForMaster(fieldsMap, appointment, email, lang);
        } else if (role.equals(Role.ROLE_ADMIN)) {
            addFieldsForAdmin(fieldsMap, appointment, lang);
        }
    }

    private void addFieldsForUser(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        if (email.equals(appointment.getCustomer().getEmail())) {
            fieldsMap.put("customer_name", userService.getLocalizedName(appointment.getCustomer(), lang));
            fieldsMap.put("rights_customer_name", "R");

            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
                fieldsMap.put("rights_serviceProvided", "R");
            }
        }
    }

    private void addFieldsForMaster(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        if (email.equals(appointment.getMaster().getEmail())) {
            fieldsMap.put("customer_name", userService.getLocalizedName(appointment.getCustomer(), lang));
            fieldsMap.put("rights_customer_name", "R");
            fieldsMap.put("master_name", userService.getLocalizedName(appointment.getMaster(), lang));
            fieldsMap.put("rights_master_name", "R");

            fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
            if (appointment.getServiceProvided()) {
                fieldsMap.put("rights_serviceProvided", "R");
            } else {
                fieldsMap.put("rights_serviceProvided", "W");
            }
        } else if (email.equals(appointment.getCustomer().getEmail())) {
            //that's possible when a master was a Customer for some service
            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
                fieldsMap.put("rights_serviceProvided", "R");
            }
        }
    }

    private void addFieldsForAdmin(HashMap<String, String> fieldsMap, Appointment appointment, String lang) {
        fieldsMap.put("customer_email", appointment.getCustomer().getEmail());
        fieldsMap.put("rights_customer_email", "H");

        fieldsMap.put("customer_name", userService.getLocalizedName(appointment.getCustomer(), lang) + " \n" + appointment.getCustomer().getTelNumber());
        fieldsMap.put("rights_customer_name", "R");

        fieldsMap.put("master_email", appointment.getMaster().getEmail());
        fieldsMap.put("rights_master_email", "H");

        fieldsMap.put("master_name", userService.getLocalizedName(appointment.getMaster(), lang) + " \n" + appointment.getMaster().getTelNumber());
        fieldsMap.put("rights_master_name", "R");

        fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
        fieldsMap.put("rights_serviceProvided", "R");
    }

    public AppointmentDao getAppointmentDao() {
        return appointmentDao;
    }

    public void setAppointmentDao(AppointmentDao appointmentDao) {
        this.appointmentDao = appointmentDao;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public EmailMessageService getEmailMessageService() {
        return emailMessageService;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }
}