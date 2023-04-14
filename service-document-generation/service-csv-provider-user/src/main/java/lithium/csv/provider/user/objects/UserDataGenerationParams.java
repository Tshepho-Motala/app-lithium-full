package lithium.csv.provider.user.objects;

import lithium.service.document.generation.client.objects.CommonCommandParams;

import java.util.Map;

public class UserDataGenerationParams extends CommonCommandParams {

    private final static String DATE_START = "startDate";
    private final static String DATE_END = "endDate";
    private final static String USER_GUID = "userGuid";

    private final static String GENERATION_RECORD_TYPE="record_type";

    public UserDataGenerationParams(Map<String, String> paramsMap) {
        super(paramsMap);
    }

    public String getUserGuid() {
        return getParamsMap().get(USER_GUID);
    }

    public String getGenerationRecordType() {
        return getParamsMap().get(GENERATION_RECORD_TYPE);
    }

    public String getDateStart() {
        return getParamsMap().get(DATE_START);
    }

    public String getEndDate() {
        return getParamsMap().get(DATE_END);
    }
}
