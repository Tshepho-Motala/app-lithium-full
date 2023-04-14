package lithium.service.accounting.provider.internal.data.objects.group;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class AccountReBalanceRequest {
    @NotNull
    String domainName;
    @NotNull
    String currencyCode;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    Date dateStart;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    Date endDate;
    boolean isMock = true;
    String userGuid;
}
