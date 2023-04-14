package lithium.service.cashier.provider.mercadonet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo.ConfigProperties;
import lithium.service.cashier.provider.mercadonet.service.MercadonetService;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
public class StartCashierController {
	@Autowired
	MercadonetService mnetService;
	@Autowired
	private MercadonetModuleInfo info;
	
	@RequestMapping("/cashier/startCashier")
	public Response<String> startCashier(@RequestParam("userName") String userName, 
			@RequestParam("domainName") String domainName, @RequestParam("apiToken") String apiToken)  throws Exception {
		
		ProviderClient pc = mnetService.getProviderService();
		Response<Iterable<ProviderProperty>> pp = pc.propertiesByProviderUrlAndDomainName(info.getModuleName(), domainName);
		
		String baseUrl = "";
		String instanceId = "";
		String skinId = "";
		for(ProviderProperty p : pp.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) {
				baseUrl = p.getValue();
			}
			if(p.getName().equalsIgnoreCase(ConfigProperties.INSTANCE_ID.getValue())) {
				instanceId = p.getValue();
			}
			if(p.getName().equalsIgnoreCase(ConfigProperties.SKIN_ID.getValue())) {
				skinId = p.getValue();
			}
		}
		 String cashierUrl = baseUrl+"/Pages/frmLogin.aspx?CustPIN="+userName+"&Password="+apiToken+":"+domainName;
		 cashierUrl+= "&FrontendID="+instanceId+"&Language=EN&SkinID="+skinId;
		 log.debug("mnet url call to start cashier: " + cashierUrl);
		return Response.<String>builder().data(cashierUrl).status(Status.OK).build();
	}
}
