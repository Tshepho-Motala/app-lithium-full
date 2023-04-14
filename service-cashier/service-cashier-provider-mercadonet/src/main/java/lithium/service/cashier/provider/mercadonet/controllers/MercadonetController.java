package lithium.service.cashier.provider.mercadonet.controllers;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.CashierClient;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.TransferRequest;
import lithium.service.cashier.client.objects.User;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo.ConfigProperties;
import lithium.service.cashier.provider.mercadonet.data.MnetRequest;
import lithium.service.cashier.provider.mercadonet.data.MnetRequestData;
import lithium.service.cashier.provider.mercadonet.data.MnetResponse;
import lithium.service.cashier.provider.mercadonet.data.MnetResponseData;
import lithium.service.cashier.provider.mercadonet.data.MnetResponseText;
import lithium.service.cashier.provider.mercadonet.data.TransferResult;
import lithium.service.cashier.provider.mercadonet.service.MercadonetService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.tokens.LithiumTokenUtil;

@Slf4j
@RestController
@RequestMapping(value = "/{providerUrl}/{apiKey}/{domainName}")
public class MercadonetController {
	@Autowired
	MercadonetService mnetService;
	
	//TODO: Add bonus management system in
//	@Autowired
//	private BonusNotificationController bonusNotificationController;
	
	@RequestMapping(method=RequestMethod.GET, produces="text/xml; charset=utf-8")
	public @ResponseBody MnetResponse mnet(
		@RequestParam(required=true) String customerLogin,
		@RequestParam(required=true) String customerPassword,
		@RequestParam(required=true) String udf,
		@RequestParam(required=true) String method
	) throws Exception {
		MnetRequestData mnetRequestData = new MnetRequestData();
		mnetRequestData.setCustomerLogin(customerLogin);
		mnetRequestData.setCustomerPassword(customerPassword);
		mnetRequestData.setUdf(udf);
		mnetRequestData.setMethod(method);
		MnetRequest request = new MnetRequest();
		request.setMnetRequestData(mnetRequestData);
		
		return mnetPost(request);
	}

	@RequestMapping(method=RequestMethod.POST, produces= "text/xml; charset=utf-8", consumes="application/x-www-form-urlencoded")
	public @ResponseBody String mnetPost(HttpServletRequest request) throws Exception {
		String mnetRequestString = request.getParameter("MnetRequest");
		if (mnetRequestString == null) {
			mnetRequestString = request.getParameter("GetBalance");
		}
		if (mnetRequestString == null) {
			mnetRequestString = request.getParameter("UpdatePersonalInfo");
		}
		if (mnetRequestString == null) {
			mnetRequestString = request.getParameter("Transfer");
		}
		
		log.info("mnetRequest :: "+mnetRequestString);
		MnetRequest mnetRequest = JAXB.unmarshal(new StringReader(mnetRequestString), MnetRequest.class);
		MnetResponse mnetResponse = mnetPost(mnetRequest);
		if (mnetResponse instanceof MnetResponseText) {
			return ((MnetResponseText) mnetResponse).getResponseString();
		}
		String response = "";
		StringWriter stringWriter = new StringWriter();
		JAXBContext context;
		Marshaller marshaller;
		try {
			context = JAXBContext.newInstance(MnetResponse.class, MnetResponseData.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			marshaller.marshal(mnetResponse, new StreamResult(stringWriter));
//			JAXB.marshal(mnetResponse, new StreamResult(stringWriter));
			response = stringWriter.toString();//.substring(55).replaceAll(" ", "").replaceAll("\n", "");
		} catch (Exception e) {
			log.error("Could not marshal response : "+e.getMessage());
		}
		
		log.info("mnetResponse :: "+response);
		
//		return mnetPost(mnetRequest);
		return response;
	}
	
	@RequestMapping(method=RequestMethod.POST, produces= "text/xml; charset=utf-8")
	public @ResponseBody MnetResponse mnetPost(@RequestBody MnetRequest request) throws Exception {
		MnetResponse response = new MnetResponse();
		
		try {
		String method = request.getMnetRequestData().getMethod();
		switch (method) {
			case "GetCustomerInfo":
				mnetService.getCustomerInfo(request, response);
				break;
			case "Transfer":
				mnetService.transfer(request, response);
				break;
			case "GetBalance":
				mnetService.getBalance(request, response);
				break;
			case "GetBonusSettings":
				//getBonusSettings(request, response);
				//Add this hack to make "Accept %?" not show on CC deposit
				MnetResponseText responseFake = new MnetResponseText();
				responseFake.setResponseString("ok");
				return responseFake;
				//break;
			case "UpdatePersonalInfo":
				mnetService.updatePersonalInfo(request, response);
				break;
			default:
				response.setError("-1");
				break;
		}
		log.debug("MnetResponse :: "+response);
		} catch (Exception e) {
			log.error("Problem with mnet stuff: " + request, e);
			response.setStatus(MnetResponse.STATUS_FAIL);
		}
		
		//response.setStatus(MnetResponse.STATUS_OK);
//		response.setBalance("0.00");
		return response;
	}
}
