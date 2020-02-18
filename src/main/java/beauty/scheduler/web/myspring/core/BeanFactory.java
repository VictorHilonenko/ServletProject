package beauty.scheduler.web.myspring.core;

import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.web.myspring.annotation.InjectDependency;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static beauty.scheduler.util.AppConstants.MAIN_PACKAGE;

public class BeanFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanFactory.class);

    private Map<String, Object> instancesContainer;

    public BeanFactory(Map<String, Object> instancesContainer) {
        this.instancesContainer = instancesContainer;

        makeInstances();

        injectDependencies();
    }

    public Object getInstantiatedClass(String className) {
        //NOTE: in this project we create only Singletons for injections.
        //Besides here we can create chains of Prototypes, if needed

        return instancesContainer.get(className);
    }

    private void makeInstances() {
        Reflections refClasses = new Reflections(MAIN_PACKAGE, new TypeAnnotationsScanner(), new SubTypesScanner());
        refClasses.getTypesAnnotatedWith(ServiceComponent.class).forEach(aClass -> {
            try {
                Object instance = aClass.newInstance();
                instancesContainer.put(aClass.getSimpleName(), instance);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("wrong configuration! can't create instance of " + aClass);
                throw new RuntimeException("wrong configuration! can't create instance");
            }
        });
    }

    private void injectDependencies() {
        Reflections refFields = new Reflections(MAIN_PACKAGE, new FieldAnnotationsScanner());
        refFields.getFieldsAnnotatedWith(InjectDependency.class).forEach(field -> {
            String injectFrom = field.getType().getSimpleName();
            String injectTo = field.getDeclaringClass().getSimpleName();

            if (!instancesContainer.containsKey(injectFrom) || !instancesContainer.containsKey(injectTo)) {
                LOGGER.error("wrong configuration! no service component found for " + injectFrom + " and/or " + injectTo);
                throw new RuntimeException("wrong configuration! no service component found");
            }

            Object instanceFrom = getInstantiatedClass(injectFrom);
            Object instanceTo = getInstantiatedClass(injectTo);

            try {
                ReflectUtils.set(instanceTo, field.getName(), field.getType(), instanceFrom);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("wrong configuration! no setter found for " + injectFrom + " and/or " + injectTo);
                throw new RuntimeException("wrong configuration! no setter found");
            }
        });
    }
}