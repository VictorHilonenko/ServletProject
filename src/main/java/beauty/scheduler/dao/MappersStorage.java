package beauty.scheduler.dao;

import beauty.scheduler.dao.core.EntityMapper;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;

//NOTE: i'm going to remove this class at all by writing automapping class (close to that point)
//right now extracted most such things to one place
public interface MappersStorage {

    EntityMapper<User> USER_ENTITY_MAPPER = resultSet -> new User(resultSet.getLong("users_id"),
            resultSet.getString("users_first_name_en"),
            resultSet.getString("users_last_name_en"),
            resultSet.getString("users_first_name_uk"),
            resultSet.getString("users_last_name_uk"),
            resultSet.getString("users_email"),
            resultSet.getString("users_tel_number"),
            resultSet.getString("users_password"),
            Role.lookupNotNull(resultSet.getString("users_role")),
            ServiceType.lookupNotNull(resultSet.getString("users_service_type")));

    EntityMapper<EmailMessage> EMAIL_MESSAGE_ENTITY_MAPPER = resultSet -> new EmailMessage(resultSet.getLong("email_messages_id"),
            resultSet.getString("email_messages_email"),
            resultSet.getString("email_messages_subject"),
            resultSet.getString("email_messages_text_message"),
            resultSet.getDate("email_messages_date_created").toLocalDate(),
            resultSet.getBoolean("email_messages_sent"),
            resultSet.getString("email_messages_quick_access_code"));

    EntityMapper<Appointment> APPOINTMENT_ENTITY_MAPPER = resultSet -> new Appointment(resultSet.getLong("appointments_id"),
            resultSet.getDate("appointments_appointment_date").toLocalDate(),
            resultSet.getByte("appointments_appointment_time"),
            ServiceType.lookupNotNull(resultSet.getString("appointments_service_type")),
            new User(resultSet.getLong("customer_id"),
                    resultSet.getString("customer_first_name_en"),
                    resultSet.getString("customer_last_name_en"),
                    resultSet.getString("customer_first_name_uk"),
                    resultSet.getString("customer_last_name_uk"),
                    resultSet.getString("customer_email"),
                    resultSet.getString("customer_tel_number"),
                    resultSet.getString("customer_password"),
                    Role.lookupNotNull(resultSet.getString("customer_role")),
                    ServiceType.lookupNotNull(resultSet.getString("customer_service_type"))),
            new User(resultSet.getLong("master_id"),
                    resultSet.getString("master_first_name_en"),
                    resultSet.getString("master_last_name_en"),
                    resultSet.getString("master_first_name_uk"),
                    resultSet.getString("master_last_name_uk"),
                    resultSet.getString("master_email"),
                    resultSet.getString("master_tel_number"),
                    resultSet.getString("master_password"),
                    Role.lookupNotNull(resultSet.getString("master_role")),
                    ServiceType.lookupNotNull(resultSet.getString("master_service_type"))),
            resultSet.getBoolean("appointments_service_provided"));

    EntityMapper<Feedback> FEEDBACK_ENTITY_MAPPER = resultSet -> new Feedback(resultSet.getLong("feedbacks_id"),
            new Appointment(resultSet.getLong("appointment_id"),
                    resultSet.getDate("appointment_appointment_date").toLocalDate(),
                    resultSet.getByte("appointment_appointment_time"),
                    ServiceType.lookupNotNull(resultSet.getString("appointment_service_type")),
                    new User(resultSet.getLong("customer_id"),
                            resultSet.getString("customer_first_name_en"),
                            resultSet.getString("customer_last_name_en"),
                            resultSet.getString("customer_first_name_uk"),
                            resultSet.getString("customer_last_name_uk"),
                            resultSet.getString("customer_email"),
                            resultSet.getString("customer_tel_number"),
                            resultSet.getString("customer_password"),
                            Role.lookupNotNull(resultSet.getString("customer_role")),
                            ServiceType.lookupNotNull(resultSet.getString("customer_service_type"))),
                    new User(resultSet.getLong("master_id"),
                            resultSet.getString("master_first_name_en"),
                            resultSet.getString("master_last_name_en"),
                            resultSet.getString("master_first_name_uk"),
                            resultSet.getString("master_last_name_uk"),
                            resultSet.getString("master_email"),
                            resultSet.getString("master_tel_number"),
                            resultSet.getString("master_password"),
                            Role.lookupNotNull(resultSet.getString("master_role")),
                            ServiceType.lookupNotNull(resultSet.getString("master_service_type"))),
                    resultSet.getBoolean("appointment_service_provided")),
            resultSet.getByte("feedbacks_rating"),
            resultSet.getString("feedbacks_text_message"));
}