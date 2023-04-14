package lithium.service.promo.pr.user.service;

import com.google.common.collect.Lists;
import lithium.service.promo.client.dto.FieldData;
import lithium.service.promo.client.objects.Granularity;
import lithium.service.promo.pr.user.dto.ExtraFieldType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldDataService {
    public List<FieldData> getFieldDataForType(ExtraFieldType type) {

        if (type != null) {
            switch (type) {
                case GRANULARITY:
                    return getGranularityData();
                case DAYS_OF_WEEK:
                    return getDaysOfTheWeek();
            }
        }
        return new ArrayList<>();
    }

    private List<FieldData> getGranularityData() {
        return Lists.newArrayList(Granularity.GRANULARITY_DAY, Granularity.GRANULARITY_WEEK, Granularity.GRANULARITY_MONTH, Granularity.GRANULARITY_YEAR, Granularity.GRANULARITY_TOTAL)
                .stream().map(granularity -> FieldData.builder()
                        .label(granularity.friendlyName())
                        .value(granularity.granularity().toString())
                        .build())
                .toList();
    }

    private List<FieldData> getDaysOfTheWeek() {
        return Arrays.stream(DayOfWeek.values()).map(dayOfWeek -> FieldData.builder()
                        .label(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                        .value(String.valueOf(dayOfWeek.getValue()))
                        .build())
                .toList();
    }

}
