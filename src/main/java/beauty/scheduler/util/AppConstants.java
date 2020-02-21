package beauty.scheduler.util;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public interface AppConstants {

    String MAIN_PACKAGE = "beauty.scheduler";

    //for tuning/adjustment:
    String SITE_URL = "http://localhost:8989";

    byte WORK_TIME_STARTS = 8;
    byte WORK_TIME_ENDS = 20;

    boolean APPOINTMENTS_TIME_TRAVELING_CHECK = false; //true - for prod, false - for debug

    //receive emails to:
    String DEBUG_EMAIL = "admin_email_test@ukr.net"; //"" - for prod, or any proxy email for debug

    //send emails from:
    String MAIL_HOST = "smtp.ukr.net";
    String MAIL_USERNAME = "admin_email_test@ukr.net";
    String MAIL_FROM = "Beauty scheduler <admin_email_test@ukr.net>";
    String MAIL_PASSWORD = "7zXpnqVF7MVUj6pa";
    String MAIL_PROTOCOL = "smtps";
    short MAIL_PORT = 465;

    ZoneId ZONE_ID = ZoneId.of("Europe/Kiev");

    int DEFAULT_PAGE_SIZE = 10;

    String REGEX_NOT_EMPTY_GENERAL = "^[A-Za-zА-Яа-яІіЇїЄє.,:;%!?\\-+0-9 \\s]+$";
    String REGEX_NOT_EMPTY_ENGLISH = "^[A-Za-z]+$";
    String REGEX_NOT_LT_2_ENGLISH = "^[A-Za-z0-9]{2,}$";
    String REGEX_URAINIAN = "^[А-Яа-яІіЇїЄє]*$";
    String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    String REGEX_TEL_FORMAT = "^\\+?380\\d{9}$";
    String REGEX_1_10_RANGE = "^([1-9]|10)$";
    String REGEX_NUMBERS = "^[0-9]+$";

    String ID_FIELD = "id";

    String REST_ERROR = "error";
    String REST_SUCCESS = "success";

    String NO_IDLE_MASTER_SQL_MESSAGE = "Column 'master_id' cannot be null";
    List<String> DONT_LOG_FOR_MESSAGES_LIST = Collections.singletonList(NO_IDLE_MASTER_SQL_MESSAGE);

    String MESSAGES_BUNDLE_NAME = "messages";

    String SLASH_SYMBOL = "/";
    String CURLY_BRACES_LEFT = "{";
    String CURLY_BRACES_RIGHT = "}";

    //algorithmic, no sense to change:
    String DEFAULT_TEMPLATE = "default";
    String REDIRECT = "redirect:";

    String ATTR_LANG = "lang";
    String ATTR_FORM = "form";
    String ATTR_ERRORS = "errors";

    String ATTR_USER_PRINCIPAL = "userPrincipal";

    String ATTR_ENDPOINT = "endpoint";
    String ATTR_ROUTER = "router";
    String ATTR_ACTIVE_USERS = "activeUsers";
    String ATTR_USERS = "users";
    String ATTR_ROLES = "roles";
    String ATTR_SERVICE_TYPES = "serviceTypes";

    String URI_PREFIX = "uri_";

    String TABLENAME = "{tablename}"; //this literal will be substituted for a table in runtime

}
