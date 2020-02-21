package beauty.scheduler.controller;

import beauty.scheduler.entity.enums.ServiceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static beauty.scheduler.util.AppConstants.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WelcomeControllerTest {

    @InjectMocks
    private WelcomeController instance;

    @Mock
    private HttpServletRequest request;

    @Test
    public void shouldSetAttributes() {

        instance.welcome(request);

        verify(request, times(1)).setAttribute(WORK_TIME_STARTS_ATTR, WORK_TIME_STARTS);
        verify(request, times(1)).setAttribute(WORK_TIME_ENDS_ATTR, WORK_TIME_ENDS);
        verify(request, times(1)).setAttribute(ATTR_SERVICE_TYPES, Arrays.stream(ServiceType.values()).filter(s -> s != ServiceType.NULL).toArray());
    }
}