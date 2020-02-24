package beauty.scheduler.service;

import beauty.scheduler.dao.EmailMessageDao;
import beauty.scheduler.entity.Appointment;
import beauty.scheduler.entity.EmailMessage;
import beauty.scheduler.entity.User;
import beauty.scheduler.util.ExtendedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.MAIL_USERNAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailMessageServiceTest {

    @InjectMocks
    private EmailMessageService instance;

    @Mock
    private EmailMessageDao emailMessageDao;

    @Before
    public void setUp() {

    }

    @Test
    public void shouldReturnEmailByQuickAccessCode() throws SQLException, ExtendedException {
        String quickAccessCode = "quickAccessCode";

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(MAIL_USERNAME);
        Optional<EmailMessage> optionalEmailMessage = Optional.of(emailMessage);

        when(emailMessageDao.findByQuickAccessCode(quickAccessCode)).thenReturn(optionalEmailMessage);
        when(emailMessageDao.update(emailMessage)).thenReturn(true);

        String email = instance.getEmailByQuickAccessCode(quickAccessCode);

        assertEquals(email, MAIL_USERNAME);
    }

    @Test
    public void shouldCreateEmailForProvidedService() throws SQLException, ExtendedException {
        User customer = new User();
        customer.setFirstNameEn("customer");
        customer.setEmail(MAIL_USERNAME);

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);

        EmailMessage emailMessage = instance.createEmailForProvidedService(appointment);
        instance.create(emailMessage);

        verify(emailMessageDao, times(1)).create(any(EmailMessage.class));
    }

    @Test
    public void shouldSendEmail() throws SQLException, ExtendedException {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(MAIL_USERNAME);
        emailMessage.setTextMessage("test");

        List<EmailMessage> list = new ArrayList<>();
        list.add(emailMessage);

        when(emailMessageDao.getListNotSent()).thenReturn(list);
        when(emailMessageDao.update(emailMessage)).thenReturn(true);

        instance.pushEmailSending(false);

        verify(emailMessageDao, times(1)).update(emailMessage);
    }
}