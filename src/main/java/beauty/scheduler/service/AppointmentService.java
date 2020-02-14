package beauty.scheduler.service;

import beauty.scheduler.dao.AppointmentDao;
import beauty.scheduler.dto.AppointmentDTO;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotations.InjectDependency;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//NOTE: partly ready for review
@ServiceComponent
public class AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentService.class);

    @InjectDependency
    private AppointmentDao appointmentDao;
    //TODO maybe create cross-service Bridge and not inject dependency here:
    @InjectDependency
    private EmailMessageService emailMessageService;

    public EmailMessageService getEmailMessageService() {
        return emailMessageService;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }

    public AppointmentService() {
    }

    public List<AppointmentDTO> getAllAppointmentsDTO(LocalDate start, LocalDate end, UserPrincipal userPrincipal) throws SQLException, ExtendedException {
        return appointmentDao.findByPeriod(start, end)
                .stream()
                .map(a -> appointmentToDTO(a, userPrincipal))
                .collect(Collectors.toList());
    }

    public AppointmentDTO appointmentToDTO(Appointment appointment, UserPrincipal userPrincipal) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        HashMap<String, String> fieldsMap = appointmentDTO.getMap();

        setFieldsAndRightsToDTOAccordingToPolicy(fieldsMap, appointment, userPrincipal);

        return appointmentDTO;
    }

    //fields with prefixes "rights_" are used in frontend to provide rights for separate roles
    //values can be: "H" - hidden, "R" - read only, "W" - write
    private void setFieldsAndRightsToDTOAccordingToPolicy(HashMap<String, String> fieldsMap, Appointment appointment, UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        Role role = userPrincipal.getRole();

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
            addFieldsForUser(fieldsMap, appointment, email, "en"); //TODO i18n -"-
        } else if (role.equals(Role.ROLE_MASTER)) {
            addFieldsForMaster(fieldsMap, appointment, email, "en"); //TODO i18n -"-
        } else if (role.equals(Role.ROLE_ADMIN)) {
            addFieldsForAdmin(fieldsMap, appointment);
        }
    }

    private void addFieldsForUser(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        //TODO i18n depending on String lang passed
        if (email.equals(appointment.getCustomer().getEmail())) {
            fieldsMap.put("customer_name", appointment.getCustomer().getFirstNameEn());
            fieldsMap.put("rights_customer_name", "R");

            if (appointment.getServiceProvided()) {
                fieldsMap.put("serviceProvided", appointment.getServiceProvided().toString());
                fieldsMap.put("rights_serviceProvided", "R");
            }
        }
    }

    private void addFieldsForMaster(HashMap<String, String> fieldsMap, Appointment appointment, String email, String lang) {
        //TODO i18n -"-
        if (email.equals(appointment.getMaster().getEmail())) {
            fieldsMap.put("customer_name", appointment.getCustomer().getFirstNameEn());
            fieldsMap.put("rights_customer_name", "R");
            fieldsMap.put("master_name", appointment.getMaster().getFirstNameEn());
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

    private void addFieldsForAdmin(HashMap<String, String> fieldsMap, Appointment appointment) {
        //TODO i18n -"-
        fieldsMap.put("customer_email", appointment.getCustomer().getEmail());
        fieldsMap.put("rights_customer_email", "H");

        fieldsMap.put("customer_name", appointment.getCustomer().getFirstNameEn() + "\n" + appointment.getCustomer().getTelNumber());
        fieldsMap.put("rights_customer_name", "R");

        fieldsMap.put("master_email", appointment.getMaster().getEmail());
        fieldsMap.put("rights_master_email", "H");

        fieldsMap.put("master_name", appointment.getMaster().getFirstNameEn() + "\n" + appointment.getMaster().getTelNumber());
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
}