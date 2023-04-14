package lithium.service.casino.provider.twowinpower.config;

import org.joda.time.DateTime;
import org.springframework.web.context.request.WebRequest;

import lithium.service.casino.provider.twowinpower.response.Response;
import lithium.service.casino.provider.twowinpower.response.Response.ErrorCode;
import lithium.service.casino.provider.twowinpower.service.TwoWinPowerService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
public class APIAuthentication {
	private TwoWinPowerService twpService;
	private String apiKey;
	private String providerUrl;
	private String domainName;
	private BrandsConfigurationBrand brandConfiguration;
	
	public static final String HEADER_X_MERCHANT_ID = "X-Merchant-Id";
	public static final String HEADER_X_TIMESTAMP = "X-Timestamp";
	public static final String HEADER_X_NONCE = "X-Nonce";
	public static final String HEADER_X_SIGN = "X-Sign";
	
	public String getProviderGuid() {
		return domainName+"/"+providerUrl;
	}
	
	public Response error(WebRequest webRequest) {
		if (!brandConfiguration.getMerchantId().equals(webRequest.getHeader(HEADER_X_MERCHANT_ID))) {
			return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Invalid MerchantId specified.").build();
		}
		Long tsh = new Long(webRequest.getHeader(HEADER_X_TIMESTAMP));
		Long now = DateTime.now().getMillis();
		tsh = tsh*1000;
		log.trace("header : "+tsh+" : "+new DateTime(tsh));
		log.trace("now : "+now+" : "+new DateTime(now));
		log.trace(""+(now - tsh));
		if ((now - tsh) > 30000) {
			log.warn("Expired Request Received... :: "+webRequest);
			return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Expired Request.").build();
		}
		String localSignature = twpService.buildSignature(
			brandConfiguration.getMerchantKey(),
			webRequest
		);
		log.trace(""+localSignature);
		if (!webRequest.getHeader(HEADER_X_SIGN).equals(localSignature)) {
			return Response.builder().errorCode(ErrorCode.INTERNAL_ERROR.value()).errorDescription("Invalid Signature.").build();
		}
		return null;
	}
}
