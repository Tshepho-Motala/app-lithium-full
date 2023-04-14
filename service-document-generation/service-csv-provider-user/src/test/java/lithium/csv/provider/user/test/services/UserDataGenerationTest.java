package lithium.csv.provider.user.test.services;

import lithium.csv.provider.user.enums.GenerationRecordType;
import lithium.csv.provider.user.services.UserCsvProviderAdapter;
import lithium.csv.provider.user.services.UserDataGeneration;
import lithium.csv.provider.user.services.UserLoginEventDataService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.csv.provider.services.CsvService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserDataGenerationTest {

    private UserDataGeneration userDataGeneration;

    @Mock
    private CsvService csvService;

    @Mock
    private LithiumServiceClientFactory factory;

    @Mock
    private UserLoginEventDataService userLoginEventDataService;

    @Test
    public void mustGetTheCorrectAdapter() {
        Mockito.when(userLoginEventDataService.type()).thenReturn(GenerationRecordType.LOGIN_EVENTS);
        UserCsvProviderAdapter adapter = userDataGeneration.getProviderAdapterForType(GenerationRecordType.LOGIN_EVENTS);

        Assertions.assertInstanceOf(UserLoginEventDataService.class, adapter);
    }

    @BeforeEach
    public void setup() {
        userDataGeneration = new UserDataGeneration(factory, csvService, List.of(userLoginEventDataService));
    }
}
