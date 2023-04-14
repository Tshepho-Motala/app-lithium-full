package lithium.csv.provider.threshold.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.document.generation.client.objects.CommonCommandParams;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThresholdsFilterRequestParams extends CommonCommandParams {
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String START_DATE_TIME = "startDateTime";
    public static final String END_DATE_TIME = "endDateTime";

    public static final String SELECTED_DOMAINS = "domains";

    public static final String DOMAIN = "domain";
    public static final String TYPE = "typeName";
    public static final String GRANULARITY = "granularity";
    public static final String PLAYER_GUID = "playerGuid";

    public ThresholdsFilterRequestParams(Map<String, String> paramsMap) {
        super(paramsMap);
    }

    public static ThresholdsFilterRequest buildThresholdFilter(CommonCommandParams commandParams) throws Status500InternalServerErrorException {

        ThresholdsFilterRequest filter = new ThresholdsFilterRequest();

        Map<String, String> params = commandParams.getParamsMap();
        try {
            String s = params.get(START_DATE_TIME);
            String e = params.get(END_DATE_TIME);
            String domain = params.get(DOMAIN);
            String type = params.get(TYPE);
            String granularity = params.get(GRANULARITY);
            String playerGuid = params.get(PLAYER_GUID);

            Date startDateTime = new SimpleDateFormat(SIMPLE_DATE_FORMAT).parse(s);
            Date endDateTime = new SimpleDateFormat(SIMPLE_DATE_FORMAT).parse(e);

            filter.setStartDateTime(startDateTime);
            filter.setEndDateTime(endDateTime);
            filter.setDomains(new String[]{domain});
            filter.setTypeName(type);
            filter.setGranularity(granularity);
            filter.setPlayerGuid(playerGuid);

        } catch (ParseException ex) {
            log.error("Can't parse Date" + ex.getMessage());
            throw new Status500InternalServerErrorException("Can't parse Date", ex.fillInStackTrace());
        }
        return filter;
    }


}

