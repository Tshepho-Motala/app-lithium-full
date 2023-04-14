package lithium.service.exchange.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.exchange.client.objects.ExchangeRequest;
import lithium.service.exchange.client.objects.ExchangeResponse;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {
	
	@GetMapping("/rate/{fromCurrencyCode}/{toCurrencyCode}")
	public Response<Double> rate(
		@PathVariable("fromCurrencyCode") String fromCurrencyCode, @PathVariable("toCurrencyCode") String toCurrencyCode
	) {
		ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider();
		ExchangeRate rate = provider.getExchangeRate(fromCurrencyCode, toCurrencyCode);
		return Response.<Double>builder().data(rate.getFactor().doubleValueExact()).status(Status.OK).build();
	}
	
	@PostMapping
	public Response<ExchangeResponse> exchange(@RequestBody ExchangeRequest request) {
		ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider();
		MonetaryAmount amount = 
		Monetary.getDefaultAmountFactory()
			.setCurrency(request.getFromCurrencyCode())
			.setNumber(request.getAmount().doubleValue())
			.create();
		CurrencyConversion conversion = provider.getCurrencyConversion(request.getToCurrencyCode());
		MonetaryAmount converted = amount.with(conversion);
		BigDecimal convertedAmount = new BigDecimal(converted.getNumber().doubleValueExact());
		BigDecimal deductiblePercentage = new BigDecimal(request.getDeductPercentage()).movePointLeft(2);
		BigDecimal convertedAmountAfterDeduction = convertedAmount.subtract(convertedAmount.multiply(deductiblePercentage));
		ExchangeResponse response = 
		ExchangeResponse.builder()
			.fromCurrencyCode(request.getFromCurrencyCode())
			.toCurrencyCode(request.getToCurrencyCode())
			.originalAmount(request.getAmount())
			.convertedAmount(convertedAmount.setScale(2, RoundingMode.CEILING))
			.convertedAmountAfterDeduction(convertedAmountAfterDeduction.setScale(2, RoundingMode.CEILING))
			.build();
		return Response	.<ExchangeResponse>builder().data(response).status(Status.OK).build();
	}
	
}