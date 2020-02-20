package beauty.scheduler.controller;

import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.dto.FeedbackDTO;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.service.FeedbackService;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.RequestMethod;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.*;
import beauty.scheduler.web.myspring.core.FormValidator;
import beauty.scheduler.web.myspring.core.Security;
import beauty.scheduler.web.myspring.form.FeedbackForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.*;

@ServiceComponent
public class FeedbackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackController.class);

    @InjectDependency
    private FeedbackService feedbackService;

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/feedbacks")
    @Restriction(role = Role.notAuthenticated, redirection = "/login")
    @DefaultTemplate(role = Role.ROLE_USER, template = "/WEB-INF/feedbacks_customer.jsp")
    @DefaultTemplate(role = Role.staff, template = "/WEB-INF/feedbacks_staff.jsp")
    public String feedbacksList(HttpServletRequest req, Pagination pagination) throws ExtendedException, SQLException {

        UserPrincipal userPrincipal = Security.getUserPrincipal(req);

        if (userPrincipal.getRole().equals(Role.ROLE_USER)) {
            List<FeedbackDTO> listFeedbacksDTOToLeave = feedbackService.getFeedbacksDTOToLeave(userPrincipal);
            req.setAttribute("listFeedbacksDTOToLeave", listFeedbacksDTOToLeave);
        }

        Pagination<FeedbackDTO> paginationFeedbacksDTO = feedbackService.getFeedbacksDTO(userPrincipal, pagination.getPage(), pagination.getPageSize());
        req.setAttribute("paginationFeedbacksDTO", paginationFeedbacksDTO);

        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.GET, urlPattern = "/feedbacks/{appointmentId}/{quickAccessCode}")
    @Restriction(role = Role.staff, exception = ExceptionKind.ACCESS_DENIED)
    @DefaultTemplate(template = "/WEB-INF/feedbacks_customer.jsp")
    public String feedbacksListByQuickLink(HttpServletRequest req, HttpServletResponse resp) {

        //TODO rewrite from Spring

        return DEFAULT_TEMPLATE;
    }

    @EndpointMethod(requestMethod = RequestMethod.POST, urlPattern = "/feedbacks")
    @Restriction(role = Role.notAuthenticated, redirection = "/login")
    @Restriction(role = Role.staff, exception = ExceptionKind.ACCESS_DENIED)
    @DefaultTemplate(template = "/WEB-INF/feedbacks_customer.jsp")
    public String feedbackUpdateAttempt(FeedbackForm form, HttpServletRequest req) throws SQLException, ExtendedException {

        Map<String, String> errors = new HashMap<>();
        FormValidator.validate(form, errors);

        Optional<Feedback> verifiedFeedback = feedbackService.getVerifiedFeedback(form, Security.getUserPrincipal(req));

        if (!verifiedFeedback.isPresent()) {
            FormValidator.appendError(errors, "", "error.wrongDataPassed");
        }
        if (errors.size() > 0) {
            req.setAttribute(ATTR_FORM, form);
            req.setAttribute(ATTR_ERRORS, errors);
        } else {
            feedbackService.updateFeedback(verifiedFeedback.get(), form);
        }

        feedbacksList(req, new Pagination());

        return DEFAULT_TEMPLATE;
    }

    public void setFeedbackService(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
}