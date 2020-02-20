package beauty.scheduler.service;

import org.junit.Test;

public class EmailMessageServiceTest {

    @Test
    public void pushEmailSending() {
        EmailMessageService emailMessageService = new EmailMessageService();
        emailMessageService.pushEmailSending();

        for (int i = 0; i < 100; i++) {
            System.out.println(i);
        }

    }
}