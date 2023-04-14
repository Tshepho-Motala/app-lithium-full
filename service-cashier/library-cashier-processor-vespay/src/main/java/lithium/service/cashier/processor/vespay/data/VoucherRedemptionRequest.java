package lithium.service.cashier.processor.vespay.data;

import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VoucherRedemptionRequest {
	@FormParam("apikey")
	private String apiKey;
	@FormParam("firstname")
	private String firstName;
	@FormParam("lastname")
	private String lastName;
	@FormParam("email")
	private String email;
	@FormParam("language")
	private String language;
	@FormParam("webreaderagent")
	private String webReaderAgent;
	@FormParam("ipaddress")
	private String ipAddress;
	@FormParam("traceid")
	private String traceId;
	@FormParam("username")
	private String userName;
	@FormParam("usercreateddate")
	private String userCreatedDate;
	@FormParam("userprofile")
	private String userProfile;
	@FormParam("blackbox")
	private String blackBox;
	@FormParam("vouchercode")
	private String voucherCode;
}
