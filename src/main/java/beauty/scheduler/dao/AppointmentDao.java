package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.dao.core.StatementMapper;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.User;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static beauty.scheduler.dao.MappersStorage.APPOINTMENT_ENTITY_MAPPER;
import static beauty.scheduler.util.AppConstants.*;

//NOTE: partly ready for review
@ServiceComponent
public class AppointmentDao extends GenericDao<Appointment> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentDao.class);

    public List<Appointment> findByPeriod(LocalDate start, LocalDate end) throws SQLException, ExtendedException {
        List<Appointment> list = super.getAllWhere(ps -> {
                    //real ISSUE here: Date.valueOf(start) converts 2020-02-10 to 2020-02-09 in setDate...
                    //ps.setDate(1, Date.valueOf(start));
                    //ps.setDate(2, Date.valueOf(end));
                    //so:
                    ps.setString(1, start.toString());
                    ps.setString(2, end.toString());
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
    public boolean create(Appointment entity) throws SQLException {
        LOGGER.debug("we don't use this function to create new Appointments");
        throw new SQLException("we don't use this function to create new Appointments");
        //otherwise it would be:
        //return create(ps -> {
        //    ps.setString(1, entity.getAppointmentDate().toString());
        //    ps.setByte(2, entity.getAppointmentTime());
        //    ps.setString(3, entity.getServiceType().name());
        //    ps.setLong(4, entity.getCustomer().getId());
        //    ps.setLong(5, entity.getMaster().getId());
        //    ps.setBoolean(6, entity.getServiceProvided());
        //});
    }

    public String reserveTime(Long customerId, String strDate, String strTime, String strServiceType, String lang) {
        //NOTE: this insert statement solves the next task:
        //it finds an idle Master who can provide the requested ServiceType.
        //if there is no one, there will be an exception.
        //this allows us to make "two actions at one time" (select and insert) or "rollback" (do nothing otherwise)
        //without using transactions at all

        //COMMENT: There is a "wrapper" (outer select for master_id value) in sql-statement.
        //It solves a sql-syntax problem:
        //"Error Code: 1093 You can't specify target table 'appointments' for update in FROM clause"
        //Error occurs when we don't use that "wrapper", but it starts work when we wrap it with that simple select.

        String query =
                "INSERT INTO appointments (\n" +
                        "	appointment_date, appointment_time, service_type, customer_id,\n" +
                        "	master_id\n" +
                        ") VALUES (\n" +
                        "	?, ?, ?, ?,\n" +
                        "	(SELECT\n" +
                        "		a.id\n" +
                        "	FROM\n" +
                        "		(SELECT\n" +
                        "			MIN(users.id) AS id\n" +
                        "		FROM\n" +
                        "			users\n" +
                        "		WHERE\n" +
                        "			users.role = 'ROLE_MASTER'\n" +
                        "			AND users.service_type = ?\n" +
                        "			AND users.id NOT IN (\n" +
                        "				SELECT\n" +
                        "					appointments.master_id AS id\n" +
                        "				FROM\n" +
                        "					appointments\n" +
                        "				WHERE\n" +
                        "					appointments.appointment_date = ?\n" +
                        "					AND appointments.appointment_time = ?\n" +
                        "			)\n" +
                        "		) AS a\n" +
                        "	)\n" +
                        ")";

        StatementMapper statementMapper = ps -> {
            ps.setString(1, strDate);
            ps.setString(2, strTime);
            ps.setString(3, strServiceType);
            ps.setLong(4, customerId);
            ps.setString(5, strServiceType);
            ps.setString(6, strDate);
            ps.setString(7, strTime);
        };

        try {
            create(statementMapper, query);
        } catch (SQLException e) {
            if (e.getMessage().equals(NO_IDLE_MASTER_SQL_MESSAGE)) {
                return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("warning.noIdleMaster", lang);
            } else {
                return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", lang);
            }
        }

        return "";
    }


    @Override
    public boolean update(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.debug("Update appointment: " + entity.toString());

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
        LOGGER.debug("Delete appointment: " + entity.toString());

        return delete(ps -> ps.setLong(1, entity.getId()));
    }

}