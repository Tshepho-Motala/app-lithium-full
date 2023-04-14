package lithium.service.csv.provider.services;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lithium.service.document.generation.client.objects.CsvContent;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CsvService {
    public StringWriter export(List<CsvContent> list, Class<? extends CsvContent> type) throws Exception {
        CustomMappingStrategy<CsvContent> mappingStrategy = new CustomMappingStrategy<>();
        mappingStrategy.setType(type);
        StringWriter writer = new StringWriter();
        StatefulBeanToCsvBuilder<CsvContent> builder = new StatefulBeanToCsvBuilder<>(writer);
        StatefulBeanToCsv<CsvContent> beanWriter = builder
                .withApplyQuotesToAll(false)
                .withMappingStrategy(mappingStrategy)
                .build();
        beanWriter.write(list);
        return writer;
    }

    private static class CustomMappingStrategy<CsvContent> extends ColumnPositionMappingStrategy<CsvContent> {
        @Override
        public String[] generateHeader(CsvContent bean) throws CsvRequiredFieldEmptyException {
            final int numColumns = getFieldMap().values().size();
            super.generateHeader(bean);

            String[] header = new String[numColumns];

            BeanField beanField;
            for (int i = 0; i < numColumns; i++) {
                beanField = findField(i);
                String columnHeaderName = extractHeaderName(beanField);
                header[i] = columnHeaderName;
            }
            return header;
        }

        private static String extractHeaderName(final BeanField beanField) {
            return Optional.ofNullable(beanField)
                    .map(BeanField::getField)
                    .map(field -> field.getDeclaredAnnotationsByType(CsvBindByName.class))
                    .stream()
                    .flatMap(Arrays::stream)
                    .findFirst()
                    .map(CsvBindByName::column)
                    .orElse(StringUtils.EMPTY);
        }
    }
}
