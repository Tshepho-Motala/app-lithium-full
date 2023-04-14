package lithium.csv.provider.user.objects;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lithium.service.document.generation.client.objects.CsvContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginEventData implements CsvContent {

    @CsvBindByName(column = "Login Time")
    @CsvBindByPosition(position = 0)
    private String loginDate;

    @CsvBindByName(column = "Country")
    @CsvBindByPosition(position = 1)
    private String country;

    @CsvBindByName(column = "IP Address")
    @CsvBindByPosition(position = 2)
    private String ipAddress;

    @CsvBindByName(column = "Login Status")
    @CsvBindByPosition(position = 3)
    private String status;

    @CsvBindByName(column = "Comment")
    @CsvBindByPosition(position = 4)
    private String comment;

    @CsvBindByName(column = "Logout Time")
    @CsvBindByPosition(position = 5)
    private String logoutDate;

    @CsvBindByName(column = "Duration")
    @CsvBindByPosition(position = 6)
    private String duration;

    @CsvBindByName(column = "Browser")
    @CsvBindByPosition(position = 7)
    private String browser;

    @CsvBindByName(column = "OS")
    @CsvBindByPosition(position = 8)
    private String os;

    @CsvBindByName(column = "Login ID")
    @CsvBindByPosition(position = 9)
    private Long loginId;

    @CsvBindByName(column = "User Agent")
    @CsvBindByPosition(position = 10)
    private String userAgent;

    @CsvBindByName(column = "Client Type")
    @CsvBindByPosition(position = 11)
    private String clientType;
}
