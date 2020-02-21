package beauty.scheduler.web;

import beauty.scheduler.util.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static beauty.scheduler.util.AppConstants.APP_ENCODING;
import static beauty.scheduler.util.AppConstants.ATTR_LANG;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocaleFilterTest {

    @InjectMocks
    private LocaleFilter instance;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private FilterChain chain;

    @Before
    public void setUp() {
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(ATTR_LANG)).thenReturn(LocaleUtils.getDefaultLocale().getLanguage());
    }

    @Test
    public void shouldSetCharacterEncodingFromAppConstants() throws ServletException, IOException {

        instance.doFilter(request, response, chain);

        verify(request, times(1)).setCharacterEncoding(APP_ENCODING);
        verify(response, times(1)).setCharacterEncoding(APP_ENCODING);
    }

    @Test
    public void shouldSetDefaultLangToRequest() throws ServletException, IOException {

        instance.doFilter(request, response, chain);

        verify(request, times(1)).setAttribute(ATTR_LANG, LocaleUtils.getDefaultLocale().getLanguage());
    }
}