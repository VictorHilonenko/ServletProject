package beauty.scheduler.service;

import beauty.scheduler.dao.FeedbackDao;
import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.dto.FeedbackDTO;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotation.InjectDependency;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import beauty.scheduler.web.myspring.form.FeedbackForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ServiceComponent
public class FeedbackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackService.class);

    @InjectDependency
    private FeedbackDao feedbackDao;

    public FeedbackService() {
    }

    public void createNewFeedbacksOnProvidedServicesForCustomer(Long customerId) throws SQLException {
        feedbackDao.createNewFeedbacksOnProvidedServicesForCustomer(customerId);
    }

    public List<FeedbackDTO> getFeedbacksDTOToLeave(UserPrincipal userPrincipal) throws ExtendedException, SQLException {
        //it's redundant check, but reliability is important, so:
        Long customerId = userPrincipal.getId()
                .orElseThrow(() -> new ExtendedException(ExceptionKind.WRONG_DATA_PASSED));
        //

        //NOTE: when a user has to leave a feedback, we create record(s) for that here
        // but only one time
        //
        //generally speaking there are 3 strategies to do that in this project logic
        //all have advantages and disadvantages.
        //can explain, why this way is chosen
        createNewFeedbacksOnProvidedServicesForCustomer(customerId);
        //

        return feedbackDao.getFeedbacksToLeave(customerId).stream()
                .map(feedback -> entityToDTOMapper(feedback, userPrincipal))
                .collect(Collectors.toList());
    }

    public Pagination<FeedbackDTO> getFeedbacksDTO(UserPrincipal userPrincipal, int page, int pageSize) throws ExtendedException, SQLException {
        Long userId = userPrincipal.getId()
                .orElseThrow(() -> new ExtendedException(ExceptionKind.WRONG_DATA_PASSED));

        Pagination<Feedback> pagination;

        if (userPrincipal.getRole().equals(Role.ROLE_USER)) {
            pagination = feedbackDao.getAllForUser(userId, page, pageSize);
        } else if (userPrincipal.getRole().equals(Role.ROLE_MASTER)) {
            pagination = feedbackDao.getAllForMaster(userId, page, pageSize);
        } else if (userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
            pagination = feedbackDao.getAllForAdmin(page, pageSize);
        } else {
            throw new ExtendedException(ExceptionKind.NOT_ACCEPTABLE_DATA_PASSED);
        }

        List<FeedbackDTO> itemsDTO = pagination.getItems().stream()
                .map(feedback -> entityToDTOMapper(feedback, userPrincipal))
                .collect(Collectors.toList());

        return new Pagination<>(page, pageSize, pagination.getTotalRecords(), itemsDTO);
    }

    private FeedbackDTO entityToDTOMapper(Feedback feedback, UserPrincipal userPrincipal) {
        return new FeedbackDTO(feedback.getId(),
                feedback.getAppointment().getAppointmentDate(), //TODO localized dates
                feedback.getAppointment().getAppointmentTime(),
                feedback.getAppointment().getServiceType().getI18n(),
                UserService.getLocalizedName(feedback.getAppointment().getCustomer(), userPrincipal.getCurrentLang()),
                UserService.getLocalizedName(feedback.getAppointment().getMaster(), userPrincipal.getCurrentLang()),
                feedback.getRating(),
                feedback.getTextMessage());
    }

    //plenty of checks, actually
    public Optional<Feedback> getVerifiedFeedback(FeedbackForm form, UserPrincipal userPrincipal) throws SQLException, ExtendedException {
        if (!userPrincipal.getId().isPresent()) {
            return Optional.empty();
        }

        Long feedbackId = Long.parseLong(form.getId());

        Optional<Feedback> optionalFeedback = feedbackDao.getById(feedbackId);
        if (!optionalFeedback.isPresent()) {
            return Optional.empty();
        }

        Feedback feedback = optionalFeedback.get();

        if (!feedback.getAppointment().getCustomer().getId().equals(userPrincipal.getId().get())) {
            return Optional.empty();
        }

        if (feedback.getRating() > 0 || !"".equals(feedback.getTextMessage())) {
            //in this business logic we don't allow "rewriting" in any case, so
            return Optional.empty();
        }

        return optionalFeedback;
    }

    public void updateFeedback(Feedback feedback, FeedbackForm form) throws SQLException, ExtendedException {
        Byte rating = Byte.parseByte(form.getRating());
        feedback.setRating(rating);
        feedback.setTextMessage(form.getTextMessage());

        feedbackDao.update(feedback);
    }

    public void setFeedbackDao(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }
}