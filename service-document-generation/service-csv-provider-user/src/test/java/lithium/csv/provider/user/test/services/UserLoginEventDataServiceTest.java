package lithium.csv.provider.user.test.services;

import lithium.csv.provider.user.config.CsvUserProviderConfigurationProperties;
import lithium.csv.provider.user.enums.GenerationRecordType;
import lithium.csv.provider.user.services.UserLoginEventDataService;
import lithium.service.user.client.service.LoginEventClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserLoginEventDataServiceTest {

    private UserLoginEventDataService userLoginEventDataService;

    @Mock
    private LoginEventClientService loginEventClientService;

    @Mock
    protected CsvUserProviderConfigurationProperties properties;

    @Test
    public void mustBeAssociatedWithTheCorrectRecordType() {
        Assertions.assertEquals(GenerationRecordType.LOGIN_EVENTS, userLoginEventDataService.type());
    }

    @BeforeEach
    public void setup() {
        userLoginEventDataService = new UserLoginEventDataService(loginEventClientService, properties);
    }
}
