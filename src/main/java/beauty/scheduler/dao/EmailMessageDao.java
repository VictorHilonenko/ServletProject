package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//NOTE: not ready for review
@ServiceComponent
public class EmailMessageDao extends GenericDao<EmailMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageDao.class);

    //TODO rewrite from Spring project

    @Override
    public Optional<EmailMessage> getById(Long id) throws SQLException, ExtendedException {
        return Optional.empty();
    }

    @Override
    public List<EmailMessage> getAll() throws SQLException, ExtendedException {
        return null;
    }

    @Override
    public boolean create(EmailMessage entity) throws SQLException, ExtendedException {
        return false;
    }

    @Override
    public boolean update(EmailMessage entity) throws SQLException, ExtendedException {
        return false;
    }

    @Override
    public boolean delete(EmailMessage entity) throws SQLException, ExtendedException {
        return false;
    }
}