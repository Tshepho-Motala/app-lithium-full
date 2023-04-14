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
public class RouterTransactionRequest {
	@FormParam("apikey")
	private String apiKey;
	@FormParam("firstname")
	private String firstName;
	@FormParam("lastname")
	private String lastName;
	@FormParam("email")
	private String email;
	@FormParam("address")
	private String address;
	@FormParam("postcode")
	private String postCode;
	@FormParam("city")
	private String city;
	@FormParam("countrycode")
	private String countryCode;
	@FormParam("statecode")
	private String stateCode;
	@FormParam("phonehome")
	private String phoneHome;
	@FormParam("phonemobile")
	private String phoneMobile;
	@FormParam("language")
	private String language;
	@FormParam("webreaderagent")
	private String webReaderAgent;
	@FormParam("ipaddress")
	private String ipAddress;
	@FormParam("traceid")
	private String traceId;
	@FormParam("username")
	private String username;
	@FormParam("usercreateddate")
	private String userCreatedDate;
	@FormParam("userprofile")
	private String userProfile;
	@FormParam("blackbox")
	private String blackbox;
	@FormParam("amount")
	private Integer amount;
	@FormParam("currency")
	private String currency;
}