package lithium.service.casino.provider.iforium.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lithium.service.casino.provider.iforium.util.extension.CapturedOutput;
import lithium.service.casino.provider.iforium.util.extension.OutputCaptureExtension;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class LoggingFilterTest {

    private static final String REQUEST_PATH = "/v1.0/balance";
    private static final String MULTI_VALUE_HEADER_NAME = "MultiValue";
    private static final String VALUE_1 = "value1";
    private static final String VALUE_2 = "value2";

    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void beforeEach() {
        this.loggingFilter = new LoggingFilter();
    }

    @Test
    @SneakyThrows
    void doFilterInternal_LogsRequestAndResponse(CapturedOutput capturedOutput) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest(HttpMethod.GET.name(), REQUEST_PATH);
        mockHttpServletRequest.setQueryString("q=abc");
        mockHttpServletRequest.addHeader(MULTI_VALUE_HEADER_NAME, VALUE_1);
        mockHttpServletRequest.addHeader(MULTI_VALUE_HEADER_NAME, VALUE_2);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        mockHttpServletResponse.setStatus(HttpStatus.OK.value());
        mockHttpServletResponse.addHeader(MULTI_VALUE_HEADER_NAME, VALUE_1);
        mockHttpServletResponse.addHeader(MULTI_VALUE_HEADER_NAME, VALUE_2);

        loggingFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, filterChain);

        Assertions.assertThat(capturedOutput.getOut())
                  .contains("request GET /v1.0/balance?q=abc, headers=[MultiValue:\"value1\", \"value2\"], body=")
                  .contains("response httpStatusCode=200, headers=[MultiValue:\"value1\", \"value2\"], body=");
    }

    @Test
    @SneakyThrows
    void doFilterInternal_LogsRequestWithoutQueryString(CapturedOutput capturedOutput) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest(HttpMethod.GET.name(), REQUEST_PATH);

        loggingFilter.doFilterInternal(mockHttpServletRequest, new MockHttpServletResponse(), filterChain);

        Assertions.assertThat(capturedOutput.getOut())
                  .contains("request GET /v1.0/balance, headers=[], body=");
    }

    @Test
    @SneakyThrows
    void doFilterInternal_DoesNotLogRequestWhenDebugIsDisabled(CapturedOutput capturedOutput) {
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingFilter.class);
        Level defaultLogLevel = logger.getLevel();
        logger.setLevel(Level.INFO);

        try {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest(HttpMethod.GET.name(), REQUEST_PATH);

            loggingFilter.doFilterInternal(mockHttpServletRequest, new MockHttpServletResponse(), filterChain);

            Assertions.assertThat(capturedOutput.getOut())
                      .isEmpty();
        } finally {
            logger.setLevel(defaultLogLevel);
        }
    }

}