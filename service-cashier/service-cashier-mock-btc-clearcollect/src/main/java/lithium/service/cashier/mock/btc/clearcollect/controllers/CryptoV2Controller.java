package lithium.service.cashier.mock.btc.clearcollect.controllers;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lithium.service.cashier.mock.btc.clearcollect.Configuration;
import lithium.service.cashier.processor.btc.clearcollect.data.DepositRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.DepositResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.GetDepositRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.GetDepositResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.GetWithdrawalRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.GetWithdrawalResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestOrResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.WithdrawRequest;
import lithium.service.cashier.processor.btc.clearcollect.data.WithdrawResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/crypto/v2")
@Slf4j
public class CryptoV2Controller {
	
	@Autowired Configuration config;
	
	@RequestMapping("/deposit/new/json")
	public DepositResponse depositNew(WebRequest webRequest) throws Exception {
		
		RequestOrResponse<DepositRequest> request = new RequestOrResponse<>(config.getApiSecret());
		request.payloadFromHeaders(webRequest, DepositRequest.class);
		
		Long amountCents = Long.parseLong(request.getData().getAmountUsdCents());
		log.info("Amount cents " + amountCents);
		double amountBtc = amountCents.floatValue() / 6400000.00;
		log.info("Amount btc " + amountBtc);
		Long amountSatoshis = Math.round(amountBtc / 0.00000001);
		log.info("Amount satoshis: " + amountSatoshis);
		
		DepositResponse response = new DepositResponse();
		response.setSuccess("true");
		
		response.setDeposit(new DepositResponse.DepositData());
		response.getDeposit().setAddress("1812xUMpCLvEdJgJxEy4XYTTDBsXVRquxM");
		response.getDeposit().setId(new Long(new Date().getTime()).toString());
		response.getDeposit().setRequestAmountBtcSatoshis(amountSatoshis.toString());
		
		log.info("NewDeposit request " + request.getData().toString() + " response " + response);
		return response;
	}
	
	@RequestMapping("/deposit/get/json")
	public GetDepositResponse getDeposit(WebRequest webRequest) throws Exception {

		RequestOrResponse<GetDepositRequest> request = new RequestOrResponse<>(config.getApiSecret());
		request.payloadFromHeaders(webRequest, GetDepositRequest.class);
		
		GetDepositResponse response = new GetDepositResponse();
		response.setSuccess("true");
		response.setDeposit(new GetDepositResponse.DepositData());
		response.getDeposit().setStatus("P");
		response.getDeposit().setCreditAmountUsdInt("0");
		
		//"EXPIRATIONDATETIME":"November, 02 2017 05:14:46"
		try {
			DateTime now = DateTime.now().plusMinutes(5);
			DateTimeFormatter f = DateTimeFormat.forPattern("MMMMM, dd yyyy HH:mm:ss");
			response.getDeposit().setExpirationDateTime(f.print(now));
			response.getDeposit().setMinutesToExpire("5");
		} catch (Exception e) {
			log.error("Could not parse expiry date "+e.getMessage(), e);
			response.getDeposit().setExpirationDateTime(null);
		}
		
		log.info("GetDeposit request " + request.getData().toString() + " response " + response);
		return response;
	}
	
	@RequestMapping("/withdraw/new/json")
	public WithdrawResponse newWithdraw(WebRequest webRequest) throws Exception {
		
		RequestOrResponse<WithdrawRequest> request = new RequestOrResponse<>(config.getApiSecret());
		request.payloadFromHeaders(webRequest, WithdrawRequest.class);
		
		Long amountCents = Long.parseLong(request.getData().getAmountUsdInt());
		log.info("Amount cents " + amountCents);
		double amountBtc = amountCents.floatValue() / 6400000.00;
		log.info("Amount btc " + amountBtc);
		Long amountSatoshis = Math.round(amountBtc / 0.00000001);
		log.info("Amount satoshis: " + amountSatoshis);
		
		WithdrawResponse response = new WithdrawResponse();
		response.setSuccess("true");
		response.getData().setId(new Long(new Date().getTime()).toString());
		
		log.info("NewWithdraw request " + request.getData().toString() + " response " + response);
		return response;
	}
	
	@RequestMapping("/withdraw/get/json")
	public GetWithdrawalResponse getWithdraw(WebRequest webRequest) throws Exception {

		RequestOrResponse<GetWithdrawalRequest> request = new RequestOrResponse<>(config.getApiSecret());
		request.payloadFromHeaders(webRequest, GetWithdrawalRequest.class);
		
		GetWithdrawalResponse response = new GetWithdrawalResponse();
		response.setSuccess("true");
		response.getData().setStatus("C");
		
		log.info("GetWithdraw request " + request.getData().toString() + " response " + response);
		return response;
	}

}
