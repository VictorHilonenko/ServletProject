package beauty.scheduler.dao.core;

import beauty.scheduler.util.ExtendedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static beauty.scheduler.util.AppConstants.ID_FIELD;
import static beauty.scheduler.util.AppConstants.TABLENAME;

public abstract class GenericDao<T> implements EntityDao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDao.class);

    private SQLStatementsGenerator sqlStatementsGenerator = new SQLStatementsGenerator(); //NOTE: no way to inject to an Abstract class except this

    public Optional<T> getById(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String query) throws SQLException {
        Optional<T> result = Optional.empty();

        try (Connection connection = ConnectionPoolHolder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementMapper.map(preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.first()) {
                    result = Optional.of(entityMapper.map(resultSet));
                }
            } catch (SQLException e) {
                LOGGER.error("SQLException " + preparedStatement);
                throw new SQLException();
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException connection");
            throw new SQLException();
        }

        return result;
    }

    public Optional<T> getById(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralSelectQueryFor(tClass, "WHERE " + TABLENAME + "." + ID_FIELD + " = ?");
        return getById(statementMapper, entityMapper, query);
    }

    private PreparedStatement prepareStatementSelect(StatementMapper<T> statementMapper, Connection connection, String query, Pagination pagination) throws SQLException {
        if (pagination != null) {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(query);
            sj.add("LIMIT");
            sj.add(Integer.toString(pagination.getPageSize()));
            sj.add("OFFSET");
            sj.add(Integer.toString(pagination.getPageSize() * pagination.getPage()));

            query = sj.toString();
        }

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        statementMapper.map(preparedStatement);

        return preparedStatement;
    }

    private PreparedStatement prepareStatementCount(StatementMapper<T> statementMapper, Connection connection, Pagination pagination) throws SQLException {
        PreparedStatement preparedStatement = null;

        if (pagination != null) {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(pagination.getQueryCount());

            preparedStatement = connection.prepareStatement(sj.toString());

            statementMapper.map(preparedStatement);
        }

        return preparedStatement;
    }

    public List<T> getAll(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String query, Pagination pagination) throws SQLException {
        List<T> result = new ArrayList<>();

        try (Connection connection = ConnectionPoolHolder.getConnection();
             PreparedStatement preparedStatementCount = prepareStatementCount(statementMapper, connection, pagination);
             PreparedStatement preparedStatementSelect = prepareStatementSelect(statementMapper, connection, query, pagination);
        ) {

            if (pagination != null) {
                try (ResultSet resultSet = preparedStatementCount.executeQuery()) {
                    if (resultSet.first()) {
                        pagination.setTotalRecords(resultSet.getInt(1));
                    }
                } catch (SQLException e) {
                    LOGGER.error("SQLException " + preparedStatementCount.toString());
                    throw new SQLException();
                }
            }

            try (ResultSet resultSet = preparedStatementSelect.executeQuery()) {
                while (resultSet.next()) {
                    T entity = entityMapper.map(resultSet);
                    result.add(entity);
                }
            } catch (SQLException e) {
                LOGGER.error("SQLException " + preparedStatementSelect.toString());
                throw new SQLException();
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException with connection");
            throw new SQLException();
        }

        if (pagination != null) {
            pagination.setItems(result);
        }

        return result;
    }

    public List<T> getAll(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String query) throws SQLException {
        return getAll(statementMapper, entityMapper, query, null);
    }

    public List<T> getAllWhere(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String where) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralSelectQueryFor(tClass, where);
        return getAll(statementMapper, entityMapper, query);
    }

    public List<T> getAll(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralSelectQueryFor(tClass);
        return getAll(statementMapper, entityMapper, query);
    }

    public Pagination<T> getAll(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String queryCount, String querySelect, int page, int pageSize) throws SQLException {
        Pagination<T> pagination = new Pagination(page, pageSize, queryCount);

        getAll(statementMapper, entityMapper, querySelect, pagination);

        return pagination;
    }

    public Pagination<T> getAllWhere(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, String where, int page, int pageSize) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String queryCount = sqlStatementsGenerator.getGeneralCountQueryFor(tClass, where);
        Pagination<T> pagination = new Pagination(page, pageSize, queryCount);

        String querySelect = sqlStatementsGenerator.getGeneralSelectQueryFor(tClass, where);
        getAll(statementMapper, entityMapper, querySelect, pagination);

        return pagination;
    }

    public Pagination<T> getAll(StatementMapper<T> statementMapper, EntityMapper<T> entityMapper, int page, int pageSize) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String queryCount = sqlStatementsGenerator.getGeneralCountQueryFor(tClass);
        Pagination<T> pagination = new Pagination(page, pageSize, queryCount);

        String querySelect = sqlStatementsGenerator.getGeneralSelectQueryFor(tClass);
        getAll(statementMapper, entityMapper, querySelect, pagination);

        return pagination;
    }

    public int getCount(StatementMapper<T> statementMapper, String query) throws SQLException {
        int count = 0;

        try (Connection connection = ConnectionPoolHolder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementMapper.map(preparedStatement);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.first()) {
                    count = resultSet.getInt(1);
                }
            } catch (SQLException e) {
                LOGGER.error("SQLException " + preparedStatement.toString());
                throw new SQLException();
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException connection");
            throw new SQLException();
        }

        return count;
    }

    public int getCount(StatementMapper<T> statementMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralCountQueryFor(tClass);
        return getCount(statementMapper, query);
    }

    public int getCountWhere(StatementMapper<T> statementMapper, String where) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralCountQueryFor(tClass, where);
        return getCount(statementMapper, query);
    }

    public boolean exists(String fieldName, String fieldValue) throws SQLException, ExtendedException {
        return getCountWhere(ps -> ps.setString(1, fieldValue), "WHERE " + fieldName + " = ?") > 0;
    }

    public boolean executeStatement(StatementMapper<T> statementMapper, String query) throws SQLException {
        try (Connection connection = ConnectionPoolHolder.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            statementMapper.map(preparedStatement);

            int result = preparedStatement.executeUpdate();

            return result > 0;
        } catch (SQLException e) {
            LOGGER.error("SQLException " + query);
            throw new SQLException();
        }
    }

    public boolean create(StatementMapper<T> statementMapper, String query) throws SQLException {
        return executeStatement(statementMapper, query);
    }

    public boolean create(StatementMapper<T> statementMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralInsertStatementFor(tClass);
        return create(statementMapper, query);
    }

    public boolean update(StatementMapper<T> statementMapper, String query) throws SQLException {
        return executeStatement(statementMapper, query);
    }

    public boolean update(StatementMapper<T> statementMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralUpdateStatementFor(tClass);
        return update(statementMapper, query);
    }

    public boolean delete(StatementMapper<T> statementMapper, String query) throws SQLException {
        return executeStatement(statementMapper, query);
    }

    public boolean delete(StatementMapper<T> statementMapper) throws SQLException, ExtendedException {
        Class tClass = getGenericParameterClass(this.getClass());

        String query = sqlStatementsGenerator.getGeneralDeleteStatementFor(tClass);
        return delete(statementMapper, query);
    }

    public static Class getGenericParameterClass(Class aClass, int parameterIndex) {
        return (Class) ((ParameterizedType) aClass.getGenericSuperclass()).getActualTypeArguments()[parameterIndex];
    }

    public static Class getGenericParameterClass(Class aClass) {
        return getGenericParameterClass(aClass, 0);
    }

}