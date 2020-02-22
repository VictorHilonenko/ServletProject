package beauty.scheduler.entity.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ServiceType {
    NULL("i18n.NULL"),
    HAIRDRESSING("i18n.HAIRDRESSING"),
    MAKEUP("i18n.MAKEUP"),
    COSMETOLOGY("i18n.COSMETOLOGY");

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceType.class);

    private String i18n;

    ServiceType(String i18n) {
        this.i18n = i18n;
    }

    public String getI18n() {
        return i18n;
    }

    //make safer parsing from JSON
    public static ServiceType lookupNotNull(String name) {
        try {
            return ServiceType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return ServiceType.NULL;
        }
    }

    @Override
    public String toString() {
        return name();
    }
}