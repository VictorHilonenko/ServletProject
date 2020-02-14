package beauty.scheduler.dao.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@FunctionalInterface
public interface InstanceMapper<T> {
    T map(Object instanceFrom) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;
}
