package beauty.scheduler.service;

import beauty.scheduler.dao.AppointmentDao;
import beauty.scheduler.dto.AppointmentDTO;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.InjectDependency;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
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

@ServiceComponent
public class AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentService.class);

    @InjectDependency
    private AppointmentDao appointmentDao;
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
        Map<String, String> map = StringUtils.mapFromJSON(jsonData);
        String strDate = map.getOrDefault("date", "");
        String strTime = map.getOrDefault("time", "");
        String strServiceType = map.getOrDefault("serviceType", "");

        //validate incoming data
        String message = validateAppointmentDataForAdd(strDate, strTime, strServiceType, userPrincipal);

        if (!"".equals(message)) {
            //not valid
            return message;
        }

        //try to reserve Master for the date and time, if any is available for that ServiceType
        message = appointmentDao.reserveTime(userPrincipal.getId().get(), strDate, strTime, strServiceType, userPrincipal.getCurrentLang());

        if (!"".equals(message)) {
            //db issue or no idle master
            return message;
        }

        return REST_SUCCESS;
    }

    public String validateAppointmentDataForAdd(String strDate, String strTime, String strServiceType, UserPrincipal userPrincipal) {
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
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        //check past time
        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        if (reserveDateTime.isBefore(nowDateTime)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.dateIsInThePast", userPrincipal.getCurrentLang());
        }

        //check work time
        if ((time < WORK_TIME_STARTS) || (time > WORK_TIME_ENDS)) {
            LOGGER.error("not working time " + strDateTime);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.notWorkingTime", userPrincipal.getCurrentLang());
        }

        //check service type
        try {
            strServiceType = ServiceType.valueOf(strServiceType).name();
        } catch (IllegalArgumentException e) {
            LOGGER.error("wrong service type " + strServiceType);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        return ""; //if OK
    }

    public String setServiceProvidedByJSON(String jsonData, UserPrincipal userPrincipal) {
        Map<String, String> map = StringUtils.mapFromJSON(jsonData);
        String strId = map.getOrDefault("id", "");
        String strServiceProvided = map.getOrDefault("serviceProvided", "");

        //validate incoming data
        String message = validateIncomingDataForServiceProvided(strId, strServiceProvided, userPrincipal);
        if (!"".equals(message)) {
            //not valid
            return message;
        }

        //try to find the Appointment
        Optional<Appointment> optionalAppointment;
        try {
            optionalAppointment = appointmentDao.getById(Long.parseLong(strId));
        } catch (SQLException | ExtendedException e) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", userPrincipal.getCurrentLang());
        }
        if (!optionalAppointment.isPresent()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.appointmentNotFound", userPrincipal.getCurrentLang());
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

        return saveWithTransaction(appointment, userPrincipal.getCurrentLang());
    }

    private String saveWithTransaction(Appointment appointment, String lang) {
        //try to update
        boolean updated = false;
        try {
            //TODO start transaction
            appointmentDao.update(appointment);
            emailMessageService.createEmailForProvidedService(appointment);
            //TODO commit
            updated = true;
        } catch (SQLException | ExtendedException e) {
            //TODO rollback
            LOGGER.error("SQLException updateServiceProvidedByJSON");
        }
        if (!updated) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", lang);
        }

        emailMessageService.pushEmailSending();

        return REST_SUCCESS;
    }

    public String validateIncomingDataForServiceProvided(String strId, String strServiceProvided, UserPrincipal userPrincipal) {
        //check format
        try {
            Long id = Long.parseLong(strId);
        } catch (DateTimeParseException e) {
            LOGGER.error("wrong data format of " + strId);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        if (!Boolean.parseBoolean(strServiceProvided)) {
            LOGGER.warn("attempt to set service provided unchecked");
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.youCanOnlySetItChecked", userPrincipal.getCurrentLang());
        }

        return ""; //if OK
    }

    private String validateAppointmentForSetServiceProvided(Appointment appointment, UserPrincipal userPrincipal) {
        //is it not set yet?
        if (appointment.getServiceProvided()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.alreadySet", userPrincipal.getCurrentLang());
        }

        //maybe it is in future?
        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), LocalTime.of(appointment.getAppointmentTime(), 0));

        if (appointmentDateTime.isAfter(nowDateTime)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.dateIsInFuture", userPrincipal.getCurrentLang());
        }

        //is user authenticated?
        if (!userPrincipal.getId().isPresent()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.notAuthenticated", userPrincipal.getCurrentLang());
        }

        //does he has authority?
        if (!userPrincipal.getRole().equals(Role.ROLE_MASTER)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.hasNoAuthority", userPrincipal.getCurrentLang());
        }

        //is it his/her appointment?
        if (!appointment.getMaster().getId().equals(userPrincipal.getId().get())) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.onlyMasterCanSetThis", userPrincipal.getCurrentLang());
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
            fieldsMap.put("customer_name", UserService.getLocalizedName(appointment.getCustomer(), lang));
            fieldsMap.put("rights_customer_name", "R");

            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
                fieldsMap.put("rights_serviceProvided", "R");
            }
        }
    }

    private void addFieldsForMaster(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        if (email.equals(appointment.getMaster().getEmail())) {
            fieldsMap.put("customer_name", UserService.getLocalizedName(appointment.getCustomer(), lang));
            fieldsMap.put("rights_customer_name", "R");
            fieldsMap.put("master_name", UserService.getLocalizedName(appointment.getMaster(), lang));
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

        fieldsMap.put("customer_name", UserService.getLocalizedName(appointment.getCustomer(), lang) + " \n" + appointment.getCustomer().getTelNumber());
        fieldsMap.put("rights_customer_name", "R");

        fieldsMap.put("master_email", appointment.getMaster().getEmail());
        fieldsMap.put("rights_master_email", "H");

        fieldsMap.put("master_name", UserService.getLocalizedName(appointment.getMaster(), lang) + " \n" + appointment.getMaster().getTelNumber());
        fieldsMap.put("rights_master_name", "R");

        fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
        fieldsMap.put("rights_serviceProvided", "R");
    }

    public void setAppointmentDao(AppointmentDao appointmentDao) {
        this.appointmentDao = appointmentDao;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }
}