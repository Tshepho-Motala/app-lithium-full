package lithium.service.cashier.mock.cc.qwipi.controllers;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.cashier.mock.cc.qwipi.Configuration;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentRequestS2S;
import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.QueryRequestS2S;
import lithium.service.cashier.processor.cc.qwipi.data.QueryResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.RefundRequestS2S;
import lithium.service.cashier.processor.cc.qwipi.data.RefundResponseS2S;
import lithium.service.cashier.processor.cc.qwipi.data.ValidationException;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.OperationType;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/universalS2S")
@Slf4j
public class ControllerS2S {
	
	@Autowired Configuration config;
	
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
	
	@RequestMapping("/payment")
	public PaymentResponseS2S payment(@RequestBody PaymentRequestS2S request) {
		log.info("Payment request: " + request.toString() + " expected md5 " + request.calculateMd5Info(config.getMd5Key()));
		
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
				.build();

		try {
			request.validate(config.getMd5Key());
		} catch (ValidationException ve) {
			response.setResultCode(ResultCode.FAILED.getCode().toString());
			response.setErrorCode(ve.getErrorCode().code().toString());
			response.setRemark(ve.getErrorCode().description());
			return response.saveMd5Info(config.getMd5Key());
		}
		
		response.setOrderId("123ASD");
		return response.saveMd5Info(config.getMd5Key());
	}
	
	@RequestMapping("/refund")
	public RefundResponseS2S refund(RefundRequestS2S request) {
		
		log.info("Refund request: " + request.toString() + " expected md5 " + request.calculateMd5Info(config.getMd5Key()));
		
		RefundResponseS2S response = RefundResponseS2S.builder()
				.amount(request.getAmount())
				.amountRefund(request.getAmountRefund())
				.orderId(request.getOrderId())
				.billNo(request.getBillNo())
				.errorCode(ErrorCode.E0000000.name().substring(1))
				.operation(OperationType.REFUND.getCodeString())
				.remark(ErrorCode.E0000000.description())
				.resultCode(ResultCode.PROCESSING.getCode().toString())
				.build();
		
		try {
			request.validate(config.getMd5Key());
		} catch (ValidationException ve) {
			response.setResultCode(ResultCode.FAILED.getCode().toString());
			response.setErrorCode(ve.getErrorCode().code().toString());
			response.setRemark(ve.getErrorCode().description());
			return response.saveMd5Info(config.getMd5Key());
		}
		
		return response.saveMd5Info(config.getMd5Key());
	}

	@RequestMapping("/query")
	public QueryResponseS2S query(QueryRequestS2S request) {
		
		log.info("Query request: " + request.toString() + " expected md5 " + request.calculateMd5Info(config.getMd5Key()));
		
		QueryResponseS2S response = QueryResponseS2S.builder()
				.amount("0.00")
				.orderId("00")
				.billNo(request.getBillNo())
				.errorCode(ErrorCode.E0000000.name().substring(1))
				.operation(OperationType.PAYMENT.getCodeString())
				.remark(ErrorCode.E0000000.description())
				.resultCode(ResultCode.SUCCESS.getCode().toString())
				.build();
		
		try {
			request.validate(config.getMd5Key());
		} catch (ValidationException ve) {
			response.setResultCode(ResultCode.FAILED.getCode().toString());
			response.setErrorCode(ve.getErrorCode().code().toString());
			response.setRemark(ve.getErrorCode().description());
			return response.saveMd5Info(config.getMd5Key());
		}
		
		return response.saveMd5Info(config.getMd5Key());
	}

}
