package lithium.service.cashier.mock.cc.qwipi.controllers;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import lithium.service.cashier.mock.cc.qwipi.Configuration;
import lithium.service.cashier.mock.cc.qwipi.services.BackgroundResponseService;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentRequest3DS;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.ValidationException;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.OperationType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/universal3DS")
@Slf4j
public class Controller3DS {
	
	@Autowired Configuration config;
	@Autowired BackgroundResponseService backgroundResponseService;
	
	public static ErrorCode getRandomErrorCode(String amountStr) {
		BigDecimal amount = new BigDecimal(amountStr);
		long amountCents = amount.movePointRight(2).longValue();
		if (amountCents == 5000) {
			return ErrorCode.E1000010;
		} else if (amountCents < 5000) {
			return ErrorCode.E0000000;
		} else {
			Random random = new Random();
			if (random.nextBoolean()) {
				return ErrorCode.values()[random.nextInt(ErrorCode.values().length)];
			} else {
				return ErrorCode.E0000000;
			}
		}
	}
	
	@RequestMapping("/payments")
	public ModelAndView payment(PaymentRequest3DS request) {
		log.info("3DS Payment request: " + request.toString() + " expected md5 " + request.calculateMd5Info(config.getMd5Key()));
		
		ErrorCode errorCode = getRandomErrorCode(request.getAmount());
		
		PaymentResponseS2S response = PaymentResponseS2S.builder()
				.amount(request.getAmount())
				.billNo(request.getBillNo())
				.currency(request.getCurrency())
				.dateTime(request.getDateTime())
				.merNo(request.getMerNo())
				.errorCode(errorCode.name().substring(1))
				.operation(OperationType.PAYMENT.getCodeString())
				.remark(errorCode.description())
				.billingDescriptor("NEWCO")
				.resultCode((errorCode.success())?ResultCode.SUCCESS.getCode().toString():ResultCode.FAILED.getCode().toString())
				.returnUrl(request.getReturnUrl())
				.bgReturnUrl(request.getBgReturnUrl())
				.build();

		try {
			request.validate(config.getMd5Key());
		} catch (ValidationException ve) {
			log.error(request.toString() + " " + ve.toString(), ve);
			response.setResultCode(ResultCode.FAILED.getCode().toString());
			response.setErrorCode(ve.getErrorCode().code().toString());
			response.setRemark(ve.getErrorCode().description());
			return new ModelAndView("payment", "response", response.saveMd5Info(config.getMd5Key()));
		}
		
		response.setOrderId("123ASD");
		return new ModelAndView("payment", "response", response.saveMd5Info(config.getMd5Key()));
	}

	@RequestMapping("/confirmOTP")
	public ModelAndView confirmOTP(PaymentResponseS2S response) throws RestClientException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		log.info("3DS Payment confirmOTP: " + response.toString());
		backgroundResponseService.postBackgroundResponse(response);
		return new ModelAndView("postmerchant", "response", response.saveMd5Info(config.getMd5Key()));
	}

}
