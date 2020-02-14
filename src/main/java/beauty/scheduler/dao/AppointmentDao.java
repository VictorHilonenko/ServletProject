package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.User;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static beauty.scheduler.dao.MappersStorage.APPOINTMENT_ENTITY_MAPPER;
import static beauty.scheduler.util.AppConstants.TABLENAME;

//NOTE: partly ready for review
@ServiceComponent
public class AppointmentDao extends GenericDao<Appointment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentDao.class);


//    @Query(value =
//    		"INSERT INTO `appointments` (\n" +
//    		"	`appointment_date`,\n" +
//    		"	`appointment_time`,\n" +
//    		"	`service_type`,\n" +
//    		"	`customer_id`,\n" +
//    		"	`master_id`\n" +
//    		") VALUES (\n" +
//    		"	:date,\n" +
//    		"	:time,\n" +
//    		"	:service_type,\n" +
//    		"	(SELECT\n" +
//    		"		Min(`id`)\n" +
//    		"	FROM\n" +
//    		"		`users`\n" +
//    		"	WHERE\n" +
//    		"		`email` = :customers_email\n" +
//    		"    ),\n" +
//    		"	(SELECT\n" +
//    		"		`a`.`id`\n" +
//    		"	FROM\n" +
//    		"		(SELECT\n" +
//    		"			MIN(`users`.`id`) AS `id`\n" +
//    		"		FROM\n" +
//    		"			`users`\n" +
//    		"		WHERE\n" +
//    		"			`users`.`role` = 'ROLE_MASTER'\n" +
//    		"			AND `users`.`service_type` = :service_type\n" +
//    		"			AND `users`.`id` NOT IN (\n" +
//    		"				SELECT\n" +
//    		"					`appointments`.`master_id` AS `id`\n" +
//    		"				FROM\n" +
//    		"					`appointments`\n" +
//    		"				WHERE\n" +
//    		"					`appointments`.`appointment_date` = :date\n" +
//    		"					AND `appointments`.`appointment_time` = :time\n" +
//    		"			)\n" +
//    		"		) AS `a`\n" +
//    		"	)\n" +
//    		")",
//    		nativeQuery = true)
    //Integer reserveTime(String customers_email, String date, String time, String service_type);


    public List<Appointment> findByPeriod(LocalDate start, LocalDate end) throws SQLException, ExtendedException {
        List<Appointment> list = super.getAllWhere(ps -> {
                    ps.setDate(1, Date.valueOf(start));
                    ps.setDate(2, Date.valueOf(end));
                },
                APPOINTMENT_ENTITY_MAPPER,
                "WHERE " + TABLENAME + ".appointment_date between ? and ?");
        return internEntities(list);
    }

    //NOTE: in this project if we wouldn't intern/flyweight the list of instances
    //it would not cause any issue, but anyway, it's better to do everything right
    protected List<Appointment> internEntities(List<Appointment> list) {
        Map<Long, Appointment> appointments = new HashMap<>();
        Map<Long, User> users = new HashMap<>();

        list.forEach(appointment -> {
            appointments.putIfAbsent(appointment.getId(), appointment);
            appointment = appointments.get(appointment.getId());

            users.putIfAbsent(appointment.getCustomer().getId(), appointment.getCustomer());
            appointment.setCustomer(users.get(appointment.getCustomer().getId()));

            users.putIfAbsent(appointment.getMaster().getId(), appointment.getMaster());
            appointment.setMaster(users.get(appointment.getMaster().getId()));
        });

        return list;
    }

    @Override
    public Optional<Appointment> getById(Long id) throws SQLException, ExtendedException {
        return super.getById(ps -> ps.setLong(1, id), APPOINTMENT_ENTITY_MAPPER);
    }

    @Override
    public List<Appointment> getAll() throws SQLException, ExtendedException {
        List<Appointment> list = super.getAll(ps -> {
        }, APPOINTMENT_ENTITY_MAPPER);
        return internEntities(list);
    }

    @Override
    public boolean create(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.debug("Create user: + " + entity.toString());

        return create(ps -> {
            ps.setString(1, entity.getAppointmentDate().toString());
            ps.setByte(2, entity.getAppointmentTime());
            ps.setString(3, entity.getServiceType().name());
            ps.setLong(4, entity.getCustomer().getId());
            ps.setLong(5, entity.getMaster().getId());
            ps.setBoolean(6, entity.getServiceProvided());
        });
    }

    @Override
    public boolean update(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.debug("Update user: " + entity.toString());

        return update(ps -> {
            ps.setString(1, entity.getAppointmentDate().toString());
            ps.setByte(2, entity.getAppointmentTime());
            ps.setString(3, entity.getServiceType().name());
            ps.setLong(4, entity.getCustomer().getId());
            ps.setLong(5, entity.getMaster().getId());
            ps.setBoolean(6, entity.getServiceProvided());
            ps.setLong(7, entity.getId());
        });
    }

    @Override
    public boolean delete(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.debug("Delete user: " + entity.toString());

        return delete(ps -> ps.setLong(1, entity.getId()));
    }
}