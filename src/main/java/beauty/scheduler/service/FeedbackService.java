package beauty.scheduler.service;

import beauty.scheduler.dao.FeedbackDao;
import beauty.scheduler.dao.core.Pagination;
import beauty.scheduler.dto.FeedbackDTO;
import beauty.scheduler.entity.Feedback;
import beauty.scheduler.entity.User;
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
    @InjectDependency
    private UserService userService;
    @InjectDependency
    private EmailMessageService emailMessageService;

    public FeedbackService() {
    }

    private void createNewFeedbacksOnProvidedServicesForCustomer(int customerId) throws SQLException {
        feedbackDao.createNewFeedbacksOnProvidedServicesForCustomer(customerId);
    }

    public List<FeedbackDTO> getFeedbacksDTOToLeave(UserPrincipal userPrincipal) throws ExtendedException, SQLException {
        int customerId = userPrincipal.getId()
                .orElseThrow(() -> new ExtendedException(ExceptionKind.WRONG_DATA_PASSED));

        createNewFeedbacksOnProvidedServicesForCustomer(customerId);

        return feedbackDao.getFeedbacksToLeave(customerId).stream()
                .map(feedback -> entityToDTOMapper(feedback, userPrincipal))
                .collect(Collectors.toList());
    }

    public Pagination<FeedbackDTO> getFeedbacksDTO(UserPrincipal userPrincipal, int page, int pageSize) throws ExtendedException, SQLException {
        int userId = userPrincipal.getId()
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
                feedback.getAppointment().getFormattedAppointmentDate(userPrincipal.getCurrentLang()),
                feedback.getAppointment().getAppointmentTime(),
                feedback.getAppointment().getServiceType().getI18n(),
                UserService.getLocalizedName(feedback.getAppointment().getCustomer(), userPrincipal.getCurrentLang()),
                UserService.getLocalizedName(feedback.getAppointment().getMaster(), userPrincipal.getCurrentLang()),
                feedback.getRating(),
                feedback.getTextMessage());
    }

    public Optional<User> getUserByQuickAccessCode(String quickAccessCode) throws SQLException, ExtendedException {
        String email = emailMessageService.getEmailByQuickAccessCode(quickAccessCode);

        return userService.findByEmail(email);
    }

    public Optional<Feedback> getVerifiedFeedback(FeedbackForm form, UserPrincipal userPrincipal) throws SQLException, ExtendedException {
        if (!userPrincipal.getId().isPresent()) {
            return Optional.empty();
        }

        int feedbackId = Integer.parseInt(form.getId());

        Optional<Feedback> optionalFeedback = feedbackDao.getById(feedbackId);
        if (!optionalFeedback.isPresent()) {
            return Optional.empty();
        }

        Feedback feedback = optionalFeedback.get();

        if (feedback.getAppointment().getCustomer().getId() != userPrincipal.getId().get()) {
            return Optional.empty();
        }

        if (feedback.getRating() > 0 || !"".equals(feedback.getTextMessage())) {
            return Optional.empty();
        }

        return optionalFeedback;
    }

    public void updateFeedback(Feedback feedback, FeedbackForm form) throws SQLException, ExtendedException {
        byte rating = Byte.parseByte(form.getRating());
        feedback.setRating(rating);
        feedback.setTextMessage(form.getTextMessage());

        feedbackDao.update(feedback);
    }

    public void setFeedbackDao(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }
}