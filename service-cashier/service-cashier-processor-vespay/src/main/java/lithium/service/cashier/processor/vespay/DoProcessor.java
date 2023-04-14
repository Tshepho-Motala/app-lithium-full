package lithium.service.cashier.processor.vespay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.method.vespay.DoProcessorVESPayAdapter;
import lithium.service.cashier.processor.DoProcessorContext;
import lithium.service.cashier.processor.vespay.data.RouterTransactionRequest;
import lithium.service.cashier.processor.vespay.data.RouterTransactionResponse;
import lithium.service.cashier.processor.vespay.data.VoucherRedemptionRequest;
import lithium.service.cashier.processor.vespay.data.VoucherRedemptionResponse;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProcessor extends DoProcessorVESPayAdapter {
	@Override
	protected DoProcessorResponseStatus depositStage1(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		BigDecimal amount = new BigDecimal(request.stageInputData(1, "amount"));
		DoProcessorRequestUser user = request.getUser();
		RouterTransactionRequest routerTranReq = RouterTransactionRequest.builder()
		.apiKey(request.getProperty("apiKey"))
		.firstName(user.getFirstName())
		.lastName(user.getLastName())
		.email(user.getEmail())
		.address(user.getResidentialAddress().toOneLinerFull())
		.postCode(user.getResidentialAddress().getPostalCode())
		.city(user.getResidentialAddress().getCity())
		.countryCode(user.getResidentialAddress().getCountryCode())
		.stateCode(
			user.getResidentialAddress().getAdminLevel1Code()
			.substring(user.getResidentialAddress().getAdminLevel1Code()
			.indexOf(".") + 1, user.getResidentialAddress().getAdminLevel1Code().length()))
		.phoneHome(user.getTelephoneNumber())
		.phoneMobile(user.getCellphoneNumber())
		.language("en_US")
		.webReaderAgent(user.getLastKnownUserAgent())
		.ipAddress(user.getLastKnownIP())
		.traceId(String.valueOf(request.getTransactionId()))
		.username(user.getUsername())
		.userCreatedDate(new SimpleDateFormat("MM/dd/yyyy").format(user.getCreatedDate()))
		.amount(amount.movePointRight(2).intValueExact())
		.currency(user.getCurrency())
		.userProfile("5")
		.build();
		RouterTransactionResponse routerTranRes = null;
		try {
			routerTranRes = postForObject(request, response, context, rest,
				request.getProperty("baseUrl") + "/v2/transaction/request",
				ObjectToHttpEntity.forPostFormFormParam(routerTranReq),
				RouterTransactionResponse.class
			);
			log.debug(routerTranRes.toString());
			if (routerTranRes.getApproved()) {
				String[] queryString = routerTranRes.getRedirectUrl().substring(routerTranRes.getRedirectUrl().indexOf("?") + 1,
					routerTranRes.getRedirectUrl().length()).split("&");
				Map<String, String> qsMap = new LinkedHashMap<String, String>();
				for (String q: queryString) {
					String key = q.substring(0, q.indexOf("="));
					String value = q.substring(q.indexOf("=") + 1, q.length());
					qsMap.put(key, value);
				}
				response.setIframeUrl(routerTranRes.getRedirectUrl().substring(0, routerTranRes.getRedirectUrl().indexOf("?")));
				response.setIframeMethod("GET");
				response.setIframePostData(qsMap);
				buildRawResponseLog(response, routerTranRes);
				return DoProcessorResponseStatus.IFRAMEPOST;
			} else {
				buildRawResponseLog(response, routerTranRes);
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			buildRawResponseLog(response, routerTranRes);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}
	
	@Override
	protected DoProcessorResponseStatus depositStage2(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws Exception {
		DoProcessorRequestUser user = request.getUser();
		VoucherRedemptionRequest voucherRedemptionReq = VoucherRedemptionRequest.builder()
		.apiKey(request.getProperty("apiKey"))
		.firstName(user.getFirstName())
		.lastName(user.getLastName())
		.email(user.getEmail())
		.language("en_US")
		.webReaderAgent(user.getLastKnownUserAgent())
		.ipAddress(user.getLastKnownIP())
		.traceId(String.valueOf(request.getTransactionId()))
		.userName(user.getUsername())
		.userCreatedDate(new SimpleDateFormat("MM/dd/yyyy").format(user.getCreatedDate()))
		.voucherCode(request.stageInputData(2, "vouchercode"))
		.userProfile("5")
		.build();
		VoucherRedemptionResponse voucherRedemptionRes = null;
		try {
			voucherRedemptionRes = postForObject(request, response, context, rest,
				request.getProperty("baseUrl") + "/v2/voucher/redeem",
				ObjectToHttpEntity.forPostFormFormParam(voucherRedemptionReq),
				VoucherRedemptionResponse.class
			);
			log.info("voucherRedemptionRes:: " + voucherRedemptionRes.toString());
			log.debug(voucherRedemptionRes.toString());
			if (voucherRedemptionRes.getApproved()) {
				response.setProcessorReference(String.valueOf(voucherRedemptionRes.getTransactionId()));
				response.setAmountCentsReceived(voucherRedemptionRes.getAmount());
				buildRawResponseLog(response, voucherRedemptionRes);
				return DoProcessorResponseStatus.SUCCESS;
			} else {
				buildRawResponseLog(response, voucherRedemptionRes);
				response.setMessage(new String(Base64.getDecoder().decode(voucherRedemptionRes.getMessageClient())));
				return DoProcessorResponseStatus.DECLINED;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			buildRawResponseLog(response, voucherRedemptionRes);
			return DoProcessorResponseStatus.FATALERROR;
		}
	}
}