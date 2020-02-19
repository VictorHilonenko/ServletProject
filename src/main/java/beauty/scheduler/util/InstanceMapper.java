package beauty.scheduler.util;

@FunctionalInterface
public interface InstanceMapper<T> {
    T map(Object instanceFrom) throws Exception;
}
