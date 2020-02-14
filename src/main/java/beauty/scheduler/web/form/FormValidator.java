package beauty.scheduler.web.form;

import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.StringJoiner;

//NOTE: mostly ready for review
public class FormValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormValidator.class);

    public static void validate(Object form, Map<String, String> errors) { //errors contains pairs <fieldName, messages>
        for (Field field : form.getClass().getDeclaredFields()) {
            validateField(form, field, errors);
        }
    }

    private static void validateField(Object form, Field field, Map<String, String> errors) {
        if (field.getDeclaredAnnotations().length == 0) {
            return;
        }

        String fieldName = field.getName();

        if (field.getType() != String.class) {
            LOGGER.error("Validation annotations is acceptable only on String fields. " + form.getClass() + " " + fieldName);
            appendError(errors, fieldName, "error.wrongFieldConf");
            return;
        }

        String fieldValue = "";
        try {
            fieldValue = (String) ReflectUtils.get(form, fieldName);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("Wrong field configuration " + form.getClass() + " " + fieldName);
            appendError(errors, fieldName, "error.wrongFieldConf");
            return;
        }

        //now there is only one annotation for validation:
        if (field.isAnnotationPresent(Regex.class)) {
            Regex annotation = field.getAnnotation(Regex.class);

            if (!StringUtils.matchesRegex(annotation.pattern(), fieldValue)) {
                appendError(errors, fieldName, annotation.errorMessage());
            }
        }

        //but if there will be more, put it here:
        //...
    }

    public static void appendError(Map<String, String> errors, String fieldName, String errorMessage) {
        StringJoiner sj = new StringJoiner("\n", errors.getOrDefault(fieldName, ""), errorMessage);
        errors.put(fieldName, sj.toString());
    }
}