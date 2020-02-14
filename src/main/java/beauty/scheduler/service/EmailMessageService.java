package beauty.scheduler.service;

import beauty.scheduler.dao.EmailMessageRepository;
import beauty.scheduler.web.myspring.annotations.InjectDependency;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//NOTE: not ready for review
@ServiceComponent
public class EmailMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageService.class);

    @InjectDependency
    private EmailMessageRepository emailMessageRepository;

    //TODO rewrite from Spring project

    public EmailMessageRepository getEmailMessageRepository() {
        return emailMessageRepository;
    }

    public void setEmailMessageRepository(EmailMessageRepository emailMessageRepository) {
        this.emailMessageRepository = emailMessageRepository;
    }
}