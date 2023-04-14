package lithium.csv.mail.provider.data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lithium.service.document.generation.client.objects.CsvContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EmailCSV implements CsvContent {
    @CsvBindByName(column = "Created Date")
    @CsvBindByPosition(position = 0)
    private String createDate;
    @CsvBindByName(column = "Sent Date")
    @CsvBindByPosition(position = 1)
    private String sendDate;
    @CsvBindByName(column = "From")
    @CsvBindByPosition(position = 2)
    private String from;
    @CsvBindByName(column = "To")
    @CsvBindByPosition(position = 3)
    private String to;
    @CsvBindByName(column = "Bcc")
    @CsvBindByPosition(position = 4)
    private String bcc;
    @CsvBindByName(column = "Subject")
    @CsvBindByPosition(position = 5)
    private String subject;
}
