package lithium.service.cashier.mock.hexopay.controllers;

import lithium.service.cashier.mock.hexopay.data.Scenario;
import lithium.service.cashier.mock.hexopay.data.exceptions.HexopayInvalidInputExeption;
import lithium.service.cashier.mock.hexopay.services.Simulator;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Transaction;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.hexopay.data.objects.WidgetModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Slf4j
@RestController
@Validated
public class WidgetController {
	@Autowired
	LithiumConfigurationProperties config;
	@Autowired
	private Simulator simulator;
	
	@RequestMapping(value="/widget", method=RequestMethod.GET)
	public ModelAndView getWidget(
		WebRequest webRequest,
		@RequestParam("token") String token
	) throws Exception {
		log.debug("token = " + token);
		WidgetModel model = simulator.getWidgetInitData(token);
		if(model.getReturnUrl() != null) {
			return new ModelAndView("redirect:" + model.getReturnUrl());
		}
		return new ModelAndView("widget", "model", model);
	}

	@RequestMapping(value="/widget/deposit", method=RequestMethod.GET)
	public RedirectView widgetDeposit(
		@RequestParam("token") String token,
		@RequestParam("cardNum") @Size(min=10, message = "Invalid card number") String cardNum,
		@RequestParam("name") @NotNull(message = "Name on card is required") @NotEmpty(message = "Name on card is required") String name,
		@RequestParam("month") @Min(value=1, message = "Month less then 1") @Max(value = 12, message = "Month greater then 12") int month,
		@RequestParam("year") @Min(value=1900, message = "Year less then 1") @Max(value = 2021, message = "Year greater then 2021") int year,
		@RequestParam("cvv") @NotNull(message = "cvv is required") Integer cvv,
		@RequestParam("3d_secure") boolean is3dSecure,
		@RequestParam("scenario") Scenario scenario
	) throws Exception {
    	return new RedirectView(simulator.simulateWidget(token, name, cardNum, month,
				year, cvv.toString(), is3dSecure, scenario));
	}

	@RequestMapping(name="/widget/3dsecure", method=RequestMethod.GET)
	public ModelAndView threeDSecurePage(
			@RequestParam("uid") String uid
	) {
			return new ModelAndView("threedsecure", "uid", uid);
	}

	@RequestMapping("/widget/3dsecure/result")
	public RedirectView checkThreeDSecure(@RequestParam("uid") String uid) throws Exception {
		return new RedirectView(simulator.simulateThreeDSecure(uid));
	}

	@RequestMapping("/widget/3dsecure")
	public ModelAndView threeDSecure (
			@RequestParam("uid") String uid
	) throws Exception {
		return new ModelAndView(simulator.simulateThreeDSecure(uid));
	}
}
