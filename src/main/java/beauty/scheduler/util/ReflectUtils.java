package beauty.scheduler.util;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);

    private static Map<Integer, InstanceMapper> typeMappers = new HashMap();

    static {
        typeMappers.put(pairClassHashCode(int.class, null), instanceFrom -> 0);
        typeMappers.put(pairClassHashCode(int.class, String.class), instanceFrom -> StringUtils.stringToInt((String) instanceFrom));
        typeMappers.put(pairClassHashCode(Role.class, String.class), instanceFrom -> Role.lookupNotNull((String) instanceFrom));
        typeMappers.put(pairClassHashCode(ServiceType.class, String.class), instanceFrom -> ServiceType.lookupNotNull((String) instanceFrom));

        //...this map can be extended, if necessary
        //also there is a possibility to make a special class for this purpose, but right now this will do
    }

    private static int pairClassHashCode(Class toClass, Class fromClass) {
        int result = 1;
        final int PRIME = 59;
        result = result * PRIME + toClass.hashCode();
        result = result * PRIME + (fromClass == null ? 79 : fromClass.hashCode());
        return result;
    }

    private static boolean areConvertableTypes(Class toClass, Class fromClass) {
        if (fromClass.equals(toClass)) {
            return true;
        }

        if (typeMappers.containsKey(pairClassHashCode(toClass, fromClass))) {
            return true;
        }

        if (toClass.equals(String.class)) { //we'll use toString() for that
            return true;
        }

        return false;
    }

    private static Method setterFor(String fieldName, Class instanceClass, Class toClass) {
        //NOTE: that wouldn't be completely legal access to a field
        //if we use field.setAccessible(true); and then field.set(...
        //so, let's do it through a setter if any exists:

        String setterName = StringUtils.camelCase("set", fieldName, true);

        try {
            return instanceClass.getMethod(setterName, toClass);
        } catch (NoSuchMethodException e) {
            LOGGER.error("No accessible setter " + setterName + " for " + toClass.getName() + " in " + instanceClass);
            return null;
        }
    }

    private static Method getterFor(String fieldName, Class instanceClass) {
        //NOTE: that wouldn't be completely legal access to a field
        //if we use field.setAccessible(true); and then field.set(...
        //so, let's do it through a setter if any exists:

        String getterName = StringUtils.camelCase("get", fieldName, true);

        try {
            return instanceClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            LOGGER.error("No accessible getter " + getterName + " in " + instanceClass);
            return null;
        }
    }

    public static boolean set(Object instance, Method setter, Object value) throws InvocationTargetException, IllegalAccessException {
        if (setter == null) {
            return false;
        }

        Class toClass = setter.getParameterTypes()[0];
        Class fromClass = value == null ? null : value.getClass();

        if (toClass.equals(fromClass)) {
            setter.invoke(instance, value);
            return true;
        }

        int key = pairClassHashCode(toClass, fromClass);

        if (!typeMappers.containsKey(key)) {
            if (toClass.equals(String.class)) {
                value = getAsString(value);
                setter.invoke(instance, value);
                return true;
            }

            return false;
        }

        try {
            value = typeMappers.get(key).map(value);
            setter.invoke(instance, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("Couldn't set value by " + setter.toString());
        }

        return false;
    }

    public static void set(Object instance, String fieldName, Class toClass, Object value) throws InvocationTargetException, IllegalAccessException {
        Method setter = setterFor(fieldName, instance.getClass(), toClass);
        set(instance, setter, value);
    }

    public static Object get(Object instance, Method getter) throws InvocationTargetException, IllegalAccessException {
        if (getter == null) {
            return null;
        }
        return getter.invoke(instance);
    }

    public static Object get(Object instance, String fieldName) throws InvocationTargetException, IllegalAccessException {
        Method getter = getterFor(fieldName, instance.getClass());
        return get(instance, getter);
    }

    private static Map<Method, Method> getLinkingMap(Class fromClass, Class toClass) {

        //TODO make it cacheable

        Map<String, Class> mapFrom = Arrays.stream(fromClass.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Field::getType));

        Map<Method, Method> linkingMap = new HashMap<>();

        for (Field field : toClass.getDeclaredFields()) {
            String fieldName = field.getName();

            if (!mapFrom.containsKey(fieldName)) {
                continue;
            }

            Class toFieldClass = field.getType();

            if (!areConvertableTypes(toFieldClass, mapFrom.get(fieldName))) {
                continue;
            }

            linkingMap.put(setterFor(fieldName, toClass, toFieldClass), getterFor(fieldName, fromClass));
        }

        return linkingMap;
    }

    public static Object map(Object instanceFrom, Object instanceTo) throws IllegalAccessException, InvocationTargetException {

        Map<Method, Method> linkingMap = getLinkingMap(instanceFrom.getClass(), instanceTo.getClass());

        for (Map.Entry<Method, Method> entry : linkingMap.entrySet()) {

            Object value = get(instanceFrom, entry.getValue());

            set(instanceTo, entry.getKey(), value);
        }

        return instanceTo;
    }

    private static Object getAsString(Object value) {
        if (value == null) {
            return "";
        }
        if (!value.getClass().equals(String.class)) {
            value = value.toString();
        }
        return value;
    }

    private static Object getNewInstanceFrom(Class neededClass, Object instanceFrom) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return map(instanceFrom, neededClass.newInstance());
    }

    public static <T> InstanceMapper<T> getMapper(Class neededClass) {
        return source -> (T) getNewInstanceFrom(neededClass, source);
    }
}