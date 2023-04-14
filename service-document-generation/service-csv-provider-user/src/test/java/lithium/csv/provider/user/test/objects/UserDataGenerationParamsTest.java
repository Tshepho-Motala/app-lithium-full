package lithium.csv.provider.user.test.objects;

import lithium.csv.provider.user.objects.UserDataGenerationParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class UserDataGenerationParamsTest {

    @Test
    public void shouldCorrectlyRetrieveParameters() {
        Map<String, String> paramsMap = Map.of("userGuid", "livescore/2023", "startDate", "2022-01-12",
                "endDate", "2023-01-30", "record_type", "login-events");

        UserDataGenerationParams params = new UserDataGenerationParams(paramsMap);

        Assertions.assertEquals("livescore/2023", params.getUserGuid());
        Assertions.assertEquals("2022-01-12", params.getDateStart());
        Assertions.assertEquals("2023-01-30", params.getEndDate());
        Assertions.assertEquals("login-events", params.getGenerationRecordType());
    }
}
