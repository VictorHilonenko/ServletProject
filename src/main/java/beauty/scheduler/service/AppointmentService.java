package beauty.scheduler.service;

import beauty.scheduler.dao.AppointmentDao;
import beauty.scheduler.dao.core.TransactionManager;
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

    private final String HIDDEN = "H";
    private final String READONLY = "R";
    private final String WRITE = "W";

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

        String message = validateAppointmentDataForAdd(strDate, strTime, strServiceType, userPrincipal);

        if (!"".equals(message)) {
            return message;
        }

        message = appointmentDao.reserveTime(userPrincipal.getId().get(), strDate, strTime, strServiceType, userPrincipal.getCurrentLang());

        if (!"".equals(message)) {
            return message;
        }

        return REST_SUCCESS;
    }

    private String validateAppointmentDataForAdd(String strDate, String strTime, String strServiceType, UserPrincipal userPrincipal) {
        strTime = (strTime.length() == 1 ? "0" : "") + strTime;
        String strDateTime = strDate + "T" + strTime + ":00:00.000";

        byte time;
        LocalDateTime reserveDateTime;
        try {
            time = Byte.parseByte(strTime);
            reserveDateTime = LocalDateTime.parse(strDateTime);
        } catch (DateTimeParseException e) {
            LOGGER.error("wrong data format of " + strDateTime);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        if (APPOINTMENTS_TIME_TRAVELING_CHECK && reserveDateTime.isBefore(nowDateTime)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.dateIsInThePast", userPrincipal.getCurrentLang());
        }

        if ((time < WORK_TIME_STARTS) || (time > WORK_TIME_ENDS)) {
            LOGGER.error("not working time " + strDateTime);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.notWorkingTime", userPrincipal.getCurrentLang());
        }

        try {
            strServiceType = ServiceType.valueOf(strServiceType).name();
        } catch (IllegalArgumentException e) {
            LOGGER.error("wrong service type " + strServiceType);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        return "";
    }

    public String setServiceProvidedByJSON(String jsonData, UserPrincipal userPrincipal) {
        Map<String, String> map = StringUtils.mapFromJSON(jsonData);
        String strId = map.getOrDefault("id", "");
        String strServiceProvided = map.getOrDefault("serviceProvided", "");

        String message = validateIncomingDataForServiceProvided(strId, strServiceProvided, userPrincipal);
        if (!"".equals(message)) {
            return message;
        }

        Optional<Appointment> optionalAppointment;
        try {
            optionalAppointment = appointmentDao.getById(Integer.parseInt(strId));
        } catch (SQLException | ExtendedException e) {
            LOGGER.error("couldn't get appointment by id " + e.getMessage());
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", userPrincipal.getCurrentLang());
        }
        if (!optionalAppointment.isPresent()) {
            LOGGER.error("no appointment for id " + strId);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.appointmentNotFound", userPrincipal.getCurrentLang());
        }
        Appointment appointment = optionalAppointment.get();

        message = validateAppointmentForSetServiceProvided(appointment, userPrincipal);
        if (!"".equals(message)) {
            return message;
        }

        appointment.setServiceProvided(true);

        return saveWithTransaction(appointment, userPrincipal.getCurrentLang());
    }

    private String saveWithTransaction(Appointment appointment, String lang) {
        boolean updated;

        try {
            TransactionManager.startTransaction();

            appointmentDao.update(appointment);
            emailMessageService.createEmailForProvidedService(appointment);

            TransactionManager.commit();

            updated = true;
        } catch (SQLException | ExtendedException e) {
            LOGGER.error("SQLException updateServiceProvidedByJSON " + e.getMessage());

            TransactionManager.rollback();

            updated = false;
        }

        if (!updated) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", lang);
        }

        emailMessageService.pushEmailSending();

        return REST_SUCCESS;
    }

    private String validateIncomingDataForServiceProvided(String strId, String strServiceProvided, UserPrincipal userPrincipal) {
        try {
            int id = Integer.parseInt(strId);
        } catch (DateTimeParseException e) {
            LOGGER.error("wrong data format of " + strId);
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        if (!Boolean.parseBoolean(strServiceProvided)) {
            LOGGER.warn("attempt to set service provided unchecked");
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.youCanOnlySetItChecked", userPrincipal.getCurrentLang());
        }

        return "";
    }

    private String validateAppointmentForSetServiceProvided(Appointment appointment, UserPrincipal userPrincipal) {
        if (appointment.getServiceProvided()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.alreadySet", userPrincipal.getCurrentLang());
        }

        LocalDateTime nowDateTime = LocalDateTime.now(ZONE_ID);
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), LocalTime.of(appointment.getAppointmentTime(), 0));

        if (APPOINTMENTS_TIME_TRAVELING_CHECK && appointmentDateTime.isAfter(nowDateTime)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.dateIsInFuture", userPrincipal.getCurrentLang());
        }

        if (!userPrincipal.getId().isPresent()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.notAuthenticated", userPrincipal.getCurrentLang());
        }

        if (!userPrincipal.getRole().equals(Role.ROLE_MASTER)) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.hasNoAuthority", userPrincipal.getCurrentLang());
        }

        if (appointment.getMaster().getId() != userPrincipal.getId().get()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.onlyMasterCanSetThis", userPrincipal.getCurrentLang());
        }

        return "";
    }

    private AppointmentDTO appointmentToDTO(Appointment appointment, UserPrincipal userPrincipal) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();

        setFieldsAndRightsToDTOAccordingToPolicy(appointmentDTO, appointment, userPrincipal);

        return appointmentDTO;
    }

    private void setFieldsAndRightsToDTOAccordingToPolicy(AppointmentDTO appointmentDTO, Appointment appointment, UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        Role role = userPrincipal.getRole();
        String lang = userPrincipal.getCurrentLang();
        HashMap<String, String> fieldsMap = appointmentDTO.getMap();

        fieldsMap.put("id", Integer.toString(appointment.getId()));
        fieldsMap.put("rights_id", HIDDEN);
        fieldsMap.put("date", appointment.getAppointmentDate().toString());
        fieldsMap.put("rights_date", READONLY);
        fieldsMap.put("time", Byte.toString(appointment.getAppointmentTime()));
        fieldsMap.put("rights_time", READONLY);
        fieldsMap.put("serviceType", appointment.getServiceType().name());
        fieldsMap.put("rights_serviceType", READONLY);

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
            fieldsMap.put("rights_customer_name", READONLY);

            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", Boolean.toString(appointment.getServiceProvided()));
                fieldsMap.put("rights_serviceProvided", READONLY);
            }
        }
    }

    private void addFieldsForMaster(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        if (email.equals(appointment.getMaster().getEmail())) {
            fieldsMap.put("customer_name", UserService.getLocalizedName(appointment.getCustomer(), lang));
            fieldsMap.put("rights_customer_name", READONLY);
            fieldsMap.put("master_name", UserService.getLocalizedName(appointment.getMaster(), lang));
            fieldsMap.put("rights_master_name", READONLY);

            fieldsMap.put("serviceProvided", Boolean.toString(appointment.getServiceProvided()));
            if (appointment.getServiceProvided()) {
                fieldsMap.put("rights_serviceProvided", READONLY);
            } else {
                fieldsMap.put("rights_serviceProvided", WRITE);
            }
        } else if (email.equals(appointment.getCustomer().getEmail())) {
            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", Boolean.toString(appointment.getServiceProvided()));
                fieldsMap.put("rights_serviceProvided", READONLY);
            }
        }
    }

    private void addFieldsForAdmin(HashMap<String, String> fieldsMap, Appointment appointment, String lang) {
        fieldsMap.put("customer_email", appointment.getCustomer().getEmail());
        fieldsMap.put("rights_customer_email", HIDDEN);

        fieldsMap.put("customer_name", UserService.getLocalizedName(appointment.getCustomer(), lang) + " \n" + appointment.getCustomer().getTelNumber());
        fieldsMap.put("rights_customer_name", READONLY);

        fieldsMap.put("master_email", appointment.getMaster().getEmail());
        fieldsMap.put("rights_master_email", HIDDEN);

        fieldsMap.put("master_name", UserService.getLocalizedName(appointment.getMaster(), lang) + " \n" + appointment.getMaster().getTelNumber());
        fieldsMap.put("rights_master_name", READONLY);

        fieldsMap.put("serviceProvided", Boolean.toString(appointment.getServiceProvided()));
        fieldsMap.put("rights_serviceProvided", READONLY);
    }

    public void setAppointmentDao(AppointmentDao appointmentDao) {
        this.appointmentDao = appointmentDao;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }
}