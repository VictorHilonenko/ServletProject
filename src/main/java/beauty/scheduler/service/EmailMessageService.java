package beauty.scheduler.service;

import beauty.scheduler.dao.EmailMessageDao;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.annotation.InjectDependency;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static beauty.scheduler.service.UserService.getUsersLang;
import static beauty.scheduler.util.AppConstants.*;

@ServiceComponent
public class EmailMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageService.class);

    @InjectDependency
    private EmailMessageDao emailMessageDao;

    public Optional<EmailMessage> findByEmail(String email) throws SQLException, ExtendedException {
        return emailMessageDao.findByEmail(email);
    }

    String getEmailByQuickAccessCode(String quickAccessCode) throws SQLException, ExtendedException {
        if (StringUtils.isEmpty(quickAccessCode)) {
            return "";
        }

        Optional<EmailMessage> optionalEmailMessage = emailMessageDao.findByQuickAccessCode(quickAccessCode);

        if (!optionalEmailMessage.isPresent()) {
            return "";
        }

        EmailMessage emailMessage = optionalEmailMessage.get();
        emailMessage.setQuickAccessCode("");

        if (!emailMessageDao.update(emailMessage)) {
            return "";
        }

        return emailMessage.getEmail();
    }

    void createEmailForProvidedService(Appointment appointment) throws SQLException, ExtendedException {
        String customerLang = getUsersLang(appointment.getCustomer());

        String quickAccessCode = UUID.randomUUID().toString();

        String textMessage = StringUtils.concatenator(LocaleUtils.getLocalizedMessage("i18n.leaveFeedbackHere", customerLang), " ",
                SITE_URL, "/feedbacks/", Integer.toString(appointment.getId()), "/", quickAccessCode);

        EmailMessage emailMessage = new EmailMessage(0,
                appointment.getCustomer().getEmail(),
                LocaleUtils.getLocalizedMessage("i18n.leaveFeedbackPlease", customerLang),
                textMessage,
                LocalDate.now(ZONE_ID),
                false,
                quickAccessCode);

        emailMessageDao.create(emailMessage);
    }

    void pushEmailSending() {
        Runnable runnable = () -> {
            List<EmailMessage> list;

            try {
                list = emailMessageDao.getListNotSent();
            } catch (Exception e) {
                LOGGER.error("repository issue during pushEmailSending");
                return;
            }

            list.forEach(this::sendAnEmail);
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void sendAnEmail(EmailMessage emailMessage) {
        Email email = new SimpleEmail();
        email.setHostName(MAIL_HOST);
        email.setSmtpPort(MAIL_PORT);
        email.setAuthenticator(new DefaultAuthenticator(MAIL_USERNAME, MAIL_PASSWORD));
        email.setSSLOnConnect(true);
        email.setSubject(emailMessage.getSubject());
        String emailAddress = StringUtils.isEmpty(DEBUG_EMAIL) ? emailMessage.getEmail() : DEBUG_EMAIL;

        try {
            email.setFrom(MAIL_FROM);
            email.setMsg(emailMessage.getTextMessage());
            email.addTo(emailAddress);
            email.send();
        } catch (Exception e) {
            LOGGER.error("email not sent " + emailMessage.toString());
            return;
        }

        emailMessage.setSent(true);

        try {
            emailMessageDao.update(emailMessage);
        } catch (Exception e) {
            LOGGER.error("email not updated " + emailMessage.toString());
            return;
        }
    }

    public void setEmailMessageDao(EmailMessageDao emailMessageDao) {
        this.emailMessageDao = emailMessageDao;
    }
}