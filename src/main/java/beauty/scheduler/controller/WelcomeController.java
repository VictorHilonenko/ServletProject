package beauty.scheduler.controller;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.web.myspring.annotation.DefaultTemplate;
import beauty.scheduler.web.myspring.annotation.EndpointMethod;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import beauty.scheduler.web.myspring.enums.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static beauty.scheduler.util.AppConstants.*;

@ServiceComponent
public class WelcomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/")
    @DefaultTemplate(template = "/WEB-INF/scheduler.jsp", role = Role.all)
    public String welcome(HttpServletRequest req) {
        req.setAttribute(WORK_TIME_STARTS_ATTR, WORK_TIME_STARTS);
        req.setAttribute(WORK_TIME_ENDS_ATTR, WORK_TIME_ENDS);
        req.setAttribute(ATTR_SERVICE_TYPES, Arrays.stream(ServiceType.values()).filter(s -> s != ServiceType.NULL).toArray());

        return DEFAULT_TEMPLATE;
    }
}