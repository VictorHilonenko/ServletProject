package beauty.scheduler.dao;

import beauty.scheduler.dao.core.GenericDao;
import beauty.scheduler.entity.User;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.dao.MappersStorage.USER_ENTITY_MAPPER;
import static beauty.scheduler.util.AppConstants.TABLENAME;

@ServiceComponent
public class UserDao extends GenericDao<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public Optional<User> findByEmail(String email) throws SQLException, ExtendedException {
        List<User> list = getAllWhere(ps -> ps.setString(1, email), USER_ENTITY_MAPPER, "WHERE " + TABLENAME + ".email = ?");

        return list.stream().findFirst();
    }

    @Override
    public Optional<User> getById(Long id) throws SQLException, ExtendedException {
        return super.getById(ps -> ps.setLong(1, id), USER_ENTITY_MAPPER);
    }

    @Override
    public List<User> getAll() throws SQLException, ExtendedException {
        return super.getAll(ps -> {
        }, USER_ENTITY_MAPPER);
    }

    @Override
    public boolean create(User entity) throws SQLException, ExtendedException {
        LOGGER.info("Create user: + " + entity.toString());

        return create(ps -> {
            ps.setString(1, entity.getFirstNameEn());
            ps.setString(2, entity.getLastNameEn());
            ps.setString(3, entity.getFirstNameUk());
            ps.setString(4, entity.getLastNameUk());
            ps.setString(5, entity.getEmail());
            ps.setString(6, entity.getTelNumber());
            ps.setString(7, entity.getPassword());
            ps.setString(8, entity.getRole().name());
            ps.setString(9, entity.getServiceType().name());
        });
    }

    @Override
    public boolean update(User entity) throws SQLException, ExtendedException {
        LOGGER.info("Update user: " + entity.toString());
        return update(ps -> {
            ps.setString(1, entity.getFirstNameEn());
            ps.setString(2, entity.getLastNameEn());
            ps.setString(3, entity.getFirstNameUk());
            ps.setString(4, entity.getLastNameUk());
            ps.setString(5, entity.getEmail());
            ps.setString(6, entity.getTelNumber());
            ps.setString(7, entity.getPassword());
            ps.setString(8, entity.getRole().name());
            ps.setString(9, entity.getServiceType().name());
            ps.setLong(10, entity.getId());
        });
    }

    @Override
    public boolean delete(User entity) throws SQLException, ExtendedException {
        LOGGER.info("Delete user: " + entity.toString());
        return delete(ps -> ps.setLong(1, entity.getId()));
    }
}