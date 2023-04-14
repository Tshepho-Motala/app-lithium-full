package lithium.service.kyc.provider.onfido.objects;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAddressContent {
    @CsvBindByName(column = "extractedAddress")
    private String extractedAddress;
    @CsvBindByName(column = "resultId")
    private Long resultId;
    @CsvBindByName(column = "addressLine1")
    private String addressLine1;
    @CsvBindByName(column = "city")
    private String city;
    @CsvBindByName(column = "postalCode")
    private String postalCode;
}
