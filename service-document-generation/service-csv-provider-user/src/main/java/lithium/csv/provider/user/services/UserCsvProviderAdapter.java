package lithium.csv.provider.user.services;

import lithium.csv.provider.user.enums.GenerationRecordType;
import lithium.csv.provider.user.objects.UserDataGenerationParams;
import lithium.service.csv.provider.services.CsvProviderAdapter;

public interface UserCsvProviderAdapter extends CsvProviderAdapter<UserDataGenerationParams> {
    GenerationRecordType type();
}
