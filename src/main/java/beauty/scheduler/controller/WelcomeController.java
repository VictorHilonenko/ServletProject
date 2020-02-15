package beauty.scheduler.controller;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.annotations.DefaultTemplate;
import beauty.scheduler.web.myspring.annotations.EndpointMethod;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static beauty.scheduler.util.AppConstants.*;

//NOTE: ready for review
@ServiceComponent
public class WelcomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/")
    @DefaultTemplate(template = "/scheduler.jsp", role = Role.all)
    public String welcome(HttpServletRequest req) {
        req.setAttribute("WORK_TIME_STARTS", WORK_TIME_STARTS);
        req.setAttribute("WORK_TIME_ENDS", WORK_TIME_ENDS);
        req.setAttribute(ATTR_SERVICE_TYPES, ServiceType.values());

        return DEFAULT_TEMPLATE;
    }
}