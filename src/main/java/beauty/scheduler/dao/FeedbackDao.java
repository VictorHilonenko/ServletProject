package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.dao.MappersStorage.FEEDBACK_ENTITY_MAPPER;

@ServiceComponent
public class FeedbackDao extends GenericDao<Feedback> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackDao.class);

    @Override
    public Optional<Feedback> getById(Long id) throws SQLException, ExtendedException {
        return super.getById(ps -> ps.setLong(1, id), FEEDBACK_ENTITY_MAPPER);
    }

    @Override
    public List<Feedback> getAll() throws SQLException, ExtendedException {
        return super.getAll(ps -> {
        }, FEEDBACK_ENTITY_MAPPER);
    }

    public Pagination<Feedback> getAll(int page, int pageSize) throws SQLException, ExtendedException {
        return super.getAll(ps -> {
        }, FEEDBACK_ENTITY_MAPPER, page, pageSize);
    }

    @Override
    public boolean create(Feedback entity) throws SQLException, ExtendedException {
        //not necessary in this project logic
        throw new ExtendedException(ExceptionKind.WRONG_METHOD_CALLED);
    }

    @Override
    public boolean update(Feedback entity) throws SQLException, ExtendedException {
        return update(ps -> {
            ps.setLong(1, entity.getAppointment().getId());
            ps.setByte(2, entity.getRating());
            ps.setString(3, entity.getTextMessage());
            ps.setLong(4, entity.getId());
        });
    }

    @Override
    public boolean delete(Feedback entity) throws SQLException {
        return false;
    }

    public void createNewFeedbacksOnProvidedServicesForCustomer(Long customerId) throws SQLException {
        String query =
                "INSERT INTO `feedbacks`\n" +
                        "	(`appointment_id`,\n" +
                        "    `rating`,\n" +
                        "    `text_message`)\n" +
                        "SELECT\n" +
                        "	`appointments`.`id`,\n" +
                        "	0,\n" +
                        "	''\n" +
                        "FROM\n" +
                        "	`appointments`\n" +
                        "LEFT JOIN\n" +
                        "	`feedbacks` ON `feedbacks`.`appointment_id` = `appointments`.`id`\n" +
                        "WHERE\n" +
                        "	`appointments`.`customer_id` = ?\n" +
                        "	AND `appointments`.`service_provided` = 1\n" +
                        "	AND `feedbacks`.`id` IS NULL";

        executeStatement(ps -> ps.setLong(1, customerId), query);
    }

    public List<Feedback> getFeedbacksToLeave(Long customerId) throws SQLException, ExtendedException {
        return getAllWhere(ps -> ps.setLong(1, customerId), FEEDBACK_ENTITY_MAPPER,
                "WHERE\n" +
                        "	customer_id = ?\n" +
                        "	AND rating = 0\n" +
                        "	AND text_message = ''");
    }

    public Pagination<Feedback> getAllForUser(Long userId, int page, int pageSize) throws SQLException, ExtendedException {
        return getAllWhere(ps -> ps.setLong(1, userId), FEEDBACK_ENTITY_MAPPER,
                "WHERE\n" +
                        "	customer.id = ?\n" +
                        "	AND rating > 0\n" +
                        "	AND text_message != ''", page, pageSize);
    }

    public Pagination<Feedback> getAllForMaster(Long userId, int page, int pageSize) throws SQLException, ExtendedException {
        return getAllWhere(ps -> ps.setLong(1, userId), FEEDBACK_ENTITY_MAPPER,
                "WHERE\n" +
                        "	master.id = ?\n" +
                        "	AND rating > 0\n" +
                        "	AND text_message != ''", page, pageSize);
    }

    public Pagination<Feedback> getAllForAdmin(int page, int pageSize) throws SQLException, ExtendedException {
        return getAllWhere(ps -> {
                }, FEEDBACK_ENTITY_MAPPER,
                "WHERE\n" +
                        "	rating > 0\n" +
                        "	AND text_message != ''", page, pageSize);
    }
}