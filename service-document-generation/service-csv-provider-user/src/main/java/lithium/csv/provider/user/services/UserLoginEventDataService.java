package lithium.csv.provider.user.services;


import java.util.Map;
import lithium.csv.provider.user.config.CsvUserProviderConfigurationProperties;
import lithium.csv.provider.user.enums.GenerationRecordType;
import lithium.csv.provider.user.objects.LoginEventData;
import lithium.csv.provider.user.objects.UserDataGenerationParams;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lithium.service.user.client.objects.LoginEventBO;
import lithium.service.user.client.objects.LoginEventQuery;
import lithium.service.user.client.service.LoginEventClientService;
import lithium.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginEventDataService implements UserCsvProviderAdapter {

    private final LoginEventClientService loginEventClientService;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final CsvUserProviderConfigurationProperties properties;

    @Override
    public CsvDataResponse getCsvData(UserDataGenerationParams params, int page) throws Status500InternalServerErrorException {

        LoginEventQuery query = LoginEventQuery.builder()
                .userGuid(params.getUserGuid())
                .size(properties.getProcessingJobPageSize())
                .page(page)
                .build();

        try {
            if (params.getDateStart() != null) {
                query.setStartDate(new Date(Long.parseLong(params.getDateStart())));
            }

            if (params.getEndDate() != null) {
                query.setEndDate(new Date(Long.parseLong(params.getEndDate())));
            }

        } catch (Exception e) {
            log.error("Failed while parsing date data for login event exports for user {}. {}", query.getUserGuid(), e);
        }

        DataTableResponse<LoginEventBO> response = loginEventClientService.search(query);
        return new CsvDataResponse(convertToLoginEventData(response.getData()), response.getRecordsTotalPages());
    }

    public List<LoginEventData> convertToLoginEventData(List<LoginEventBO> loginEventList) {
        return loginEventList.stream().map(event -> LoginEventData.builder()
                        .loginId(event.getId())
                        .os(event.getOs())
                        .loginDate(Optional.ofNullable(event.getDate()).map(DATE_FORMAT::format).orElse(null))
                        .country(event.getCountry())
                        .ipAddress(event.getIpAddress())
                        .userAgent(event.getUserAgent())
                        .logoutDate(Optional.ofNullable(event.getLogout()).map(DATE_FORMAT::format).orElse(null))
                        .duration(DateUtil.timestampToHumanReadable(event.getDuration()))
                        .comment(event.getComment())
                        .browser(event.getBrowser())
                        .status(Boolean.parseBoolean(event.getSuccessful()) ? "success": "fail")
                        .clientType(event.getProviderAuthClient())
                        .build())
                .toList();
    }

    @Override
    public Class<? extends CsvContent> getContentType() {
        return LoginEventData.class;
    }
    @Override
    public Class<? extends CsvContent> getContentType(Map<String, String> parameters) {
        return getContentType();
    }


    @Override
    public GenerationRecordType type() {
        return GenerationRecordType.LOGIN_EVENTS;
    }
}
