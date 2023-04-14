package lithium.csv.mail.provider.services;

import java.util.Map;
import lithium.csv.mail.provider.config.CsvMailProviderConfigurationProperties;
import lithium.csv.mail.provider.data.EmailCSV;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.csv.provider.services.CsvProviderAdapter;
import lithium.service.document.generation.client.objects.CommonCommandParams;
import lithium.service.document.generation.client.objects.CsvContent;
import lithium.service.document.generation.client.objects.CsvDataResponse;
import lithium.service.mail.client.SystemMailClient;
import lithium.service.mail.client.objects.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class MailService implements CsvProviderAdapter<CommonCommandParams> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LithiumServiceClientFactory lithiumServiceClientFactory;

    private CsvMailProviderConfigurationProperties properties;

    @Override
    public Class<? extends CsvContent> getContentType() {
        return EmailCSV.class;
    }
    @Override
    public Class<? extends CsvContent> getContentType(Map<String, String> parameters) {
        return getContentType();
    }

    @Override
    public CsvDataResponse getCsvData(CommonCommandParams params, int page) throws Status500InternalServerErrorException {


        SystemMailClient client = null;
        try {
            client = lithiumServiceClientFactory.target(SystemMailClient.class, "service-mail", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage());
            throw new Status500InternalServerErrorException(e.getMessage(), e.fillInStackTrace());
        }

        String userGuid = params.getParamsMap().get("userGuid");
        DataTableResponse<Email> emails = client.find(userGuid, page, properties.getProcessingJobPageSize());

        return new CsvDataResponse(
                collectCSVData(emails.getData()),
                emails.getRecordsTotalPages());
    }

    private static List<EmailCSV> collectCSVData(List<Email> emails) {
        return emails.stream()
                .map(email -> EmailCSV.builder()
                        .createDate(Optional.ofNullable(email.getCreatedDate()).map(DATE_FORMAT::format).orElse("N/A"))
                        .sendDate(Optional.ofNullable(email.getSentDate()).map(DATE_FORMAT::format).orElse("N/A"))
                        .from(email.getFrom())
                        .to(email.getTo())
                        .bcc(email.getBcc())
                        .subject(email.getSubject())
                        .build())
                .toList();
    }
}
