package beauty.scheduler.service;

import beauty.scheduler.dao.EmailMessageDao;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.web.myspring.annotation.InjectDependency;
import beauty.scheduler.web.myspring.annotation.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceComponent
public class EmailMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageService.class);

    @InjectDependency
    private EmailMessageDao emailMessageDao;

    //TODO rewrite from Spring project
    public void sendRequestForFeedbackToCustomer(Appointment appointment) {
        //TODO
    }

    public EmailMessageDao getEmailMessageDao() {
        return emailMessageDao;
    }

    public void setEmailMessageDao(EmailMessageDao emailMessageDao) {
        this.emailMessageDao = emailMessageDao;
    }

}