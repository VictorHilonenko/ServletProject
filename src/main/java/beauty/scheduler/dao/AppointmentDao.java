package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.dao.core.StatementMapper;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.User;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
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

    private List<Appointment> internEntities(List<Appointment> list) {
        Map<Integer, User> users = new HashMap<>();

        list.forEach(appointment -> {
            users.putIfAbsent(appointment.getCustomer().getId(), appointment.getCustomer());
            appointment.setCustomer(users.get(appointment.getCustomer().getId()));

            users.putIfAbsent(appointment.getMaster().getId(), appointment.getMaster());
            appointment.setMaster(users.get(appointment.getMaster().getId()));
        });

        return list;
    }

    @Override
    public Optional<Appointment> getById(int id) throws SQLException, ExtendedException {
        return super.getById(ps -> ps.setInt(1, id), APPOINTMENT_ENTITY_MAPPER);
    }

    @Override
    public List<Appointment> getAll() throws SQLException, ExtendedException {
        List<Appointment> list = super.getAll(ps -> {
        }, APPOINTMENT_ENTITY_MAPPER);
        return internEntities(list);
    }

    @Override
    public boolean create(Appointment entity) throws SQLException {
        LOGGER.info("we don't use this function to create new Appointments");
        throw new SQLException("we don't use this function to create new Appointments");
    }

    public String reserveTime(int customerId, String strDate, String strTime, String strServiceType, String lang) {

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
            ps.setInt(4, customerId);
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
                LOGGER.error("during reserve time get: " + e.getMessage());
                return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", lang);
            }
        }

        return "";
    }


    @Override
    public boolean update(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.info("Update appointment: " + entity.toString());

        return update(ps -> {
            ps.setString(1, entity.getAppointmentDate().toString());
            ps.setByte(2, entity.getAppointmentTime());
            ps.setString(3, entity.getServiceType().name());
            ps.setInt(4, entity.getCustomer().getId());
            ps.setInt(5, entity.getMaster().getId());
            ps.setBoolean(6, entity.getServiceProvided());
            ps.setInt(7, entity.getId());
        });
    }

    @Override
    public boolean delete(Appointment entity) throws SQLException, ExtendedException {
        LOGGER.info("Delete appointment: " + entity.toString());

        return delete(ps -> ps.setInt(1, entity.getId()));
    }

}