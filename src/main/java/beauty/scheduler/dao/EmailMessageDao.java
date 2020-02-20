package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.dao.MappersStorage.EMAIL_MESSAGE_ENTITY_MAPPER;
import static beauty.scheduler.util.AppConstants.TABLENAME;

@ServiceComponent
public class EmailMessageDao extends GenericDao<EmailMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageDao.class);

    public List<EmailMessage> findNotSent() throws SQLException, ExtendedException {
        return super.getAllWhere(ps -> {
                    ps.setBoolean(1, false);
                },
                EMAIL_MESSAGE_ENTITY_MAPPER, "WHERE " + TABLENAME + ".sent = ?");
    }

    @Override
    public Optional<EmailMessage> getById(Long id) throws SQLException, ExtendedException {
        return super.getById(ps -> ps.setLong(1, id), EMAIL_MESSAGE_ENTITY_MAPPER);
    }

    @Override
    public List<EmailMessage> getAll() throws SQLException, ExtendedException {
        return super.getAll(ps -> {
        }, EMAIL_MESSAGE_ENTITY_MAPPER);
    }

    @Override
    public boolean create(EmailMessage entity) throws SQLException, ExtendedException {
        LOGGER.info("Create EmailMessage: + " + entity.toString());

        return create(ps -> {
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getSubject());
            ps.setString(3, entity.getTextMessage());
            ps.setString(4, entity.getDateCreated().toString());
            ps.setBoolean(5, entity.getSent());
            ps.setString(6, entity.getQuickAccessCode());
        });
    }

    @Override
    public boolean update(EmailMessage entity) throws SQLException, ExtendedException {
        LOGGER.info("Update EmailMessage: " + entity.toString());

        return update(ps -> {
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getSubject());
            ps.setString(3, entity.getTextMessage());
            ps.setString(4, entity.getDateCreated().toString());
            ps.setBoolean(5, entity.getSent());
            ps.setString(6, entity.getQuickAccessCode());
            ps.setLong(7, entity.getId());
        });
    }

    @Override
    public boolean delete(EmailMessage entity) throws SQLException, ExtendedException {
        LOGGER.info("Delete EmailMessage: " + entity.toString());

        return delete(ps -> ps.setLong(1, entity.getId()));
    }
}