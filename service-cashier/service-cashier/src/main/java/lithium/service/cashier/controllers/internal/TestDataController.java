package lithium.service.cashier.controllers.internal;

import lithium.service.user.client.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.DomainService;
import lithium.service.cashier.services.MethodService;
import lithium.service.cashier.services.ProcessorService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/internal/testdata")
@Slf4j
public class TestDataController {

	@Autowired DomainService domainService;
	@Autowired DomainMethodService domainMethodService;
	@Autowired DomainMethodProcessorService domainMethodProcessorService;
	@Autowired MethodService methodService;
	@Autowired ProcessorService processorService;

	@RequestMapping("/qwipi")
	public String qwipi() throws Exception {
		log.info("Creating qwipi processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("cc");
		DomainMethod dm = domainMethodService.findOrCreate("Credit Card", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("qwipi");
		if (proc == null) throw new Exception("service-cashier-processor-cc-qwipi does not yet exist in processors table. Start service");
		DomainMethodProcessor dmpQwipi1 = domainMethodProcessorService.create(dm, proc, "Qwipi 3DS", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmpQwipi1, "3dsecure", "yes");
		DomainMethodProcessor dmpQwipi2 = domainMethodProcessorService.create(dm, proc, "Qwipi Non3DS", true, 50, "default/admin","default/admin");
		domainMethodProcessorService.setProperty(dmpQwipi2, "3dsecure", "no");
		return "OK";
	}

	@RequestMapping("/trustspay")
	public String trustspay() throws Exception {
		log.info("Creating trustspay processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("cc");
		DomainMethod dm = domainMethodService.findOrCreate("Credit Card", null, null, null, true, true, 1, method.getId(), "luckybetz",User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("trustspay");
		if (proc == null) throw new Exception("service-cashier-processor-cc-trustspay does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "Trustspay", true, 50, "default/admin", "default/admin");
//		domainMethodProcessorService.setProperty(dmp, "paymentUrl", "http://localhost:9798");
		domainMethodProcessorService.setProperty(dmp, "testMode", "true");
		return "OK";
	}

	@RequestMapping("/clearcollect")
	public String clearcollect() throws Exception {
		log.info("Creating clearcollect deposit processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("btc");
		DomainMethod dm = domainMethodService.findOrCreate("BitCoin", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("clearcollect");
		if (proc == null) throw new Exception("service-cashier-processor-btc-clearcollect does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "ClearCollect", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "apikey", "ASD123");
		return "OK";
	}
		
	@RequestMapping("/clearcollectwithdraw")
	public String clearcollectWithdraw() throws Exception {
		log.info("Creating clearcollect withdrawal processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("btc");
		DomainMethod dm = domainMethodService.findOrCreate("BitCoin", null, null, null, true, false, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("clearcollect");
		if (proc == null) throw new Exception("service-cashier-processor-btc-clearcollect does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "ClearCollect", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "apikey", "ASD123");
		return "OK";
	}


	@RequestMapping("/upay")
	public String upay() throws Exception {
		log.info("Creating upay processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("upay");
		DomainMethod dm = domainMethodService.findOrCreate("UPayCard", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("upay");
		if (proc == null) throw new Exception("service-cashier-processor-upay-upay does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "UPayCard", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "receiver_account", "1784272");
		domainMethodProcessorService.setProperty(dmp, "apiurl", "http://localhost:9202");
		return "OK";
	}

	@RequestMapping("/upaybtc")
	public String upaybtc() throws Exception {
		log.info("Creating upay btc processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("btc");
		DomainMethod dm = domainMethodService.findOrCreate("UPay Bitcoin", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("upaybtc");
		if (proc == null) throw new Exception("service-cashier-processor-upay-btc does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "UPay BTC", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "receiver_account", "1784272");
		domainMethodProcessorService.setProperty(dmp, "apiurl", "http://localhost:9202");
		return "OK";
	}

	@RequestMapping("/paymentclickswu")
	public String paymentclicksWU() throws Exception {
		log.info("Creating paymentclicks wu processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("wu");
		DomainMethod dm = domainMethodService.create("WesternUnion", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("paymentclicks");
		if (proc == null) throw new Exception("paymentclicks does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "PaymentClicks WU", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "url", "http://localhost:9000/service-cashier-mock-wumg-paymentclicks/api");
		domainMethodProcessorService.setProperty(dmp, "username", "testuser");
		domainMethodProcessorService.setProperty(dmp, "password", "password");
		return "OK";
	}

	@RequestMapping("/paymentclicksmg")
	public String paymentclicksMG() throws Exception {
		log.info("Creating paymentclicks mg processor for luckybetz");
		domainService.findOrCreateDomain("luckybetz");
		Method method = methodService.findByCode("mg");
		DomainMethod dm = domainMethodService.create("MoneyGram", null, null, null, true, true, 1, method.getId(), "luckybetz", User.SYSTEM_GUID, User.SYSTEM_FULL_NAME);
		Processor proc = processorService.findByCode("paymentclicks");
		if (proc == null) throw new Exception("paymentclicks does not yet exist in processors table. Start service");
		DomainMethodProcessor dmp = domainMethodProcessorService.create(dm, proc, "PaymentClicks MG", true, 50, "default/admin", "default/admin");
		domainMethodProcessorService.setProperty(dmp, "url", "http://localhost:9000/service-cashier-mock-wumg-paymentclicks/api");
		domainMethodProcessorService.setProperty(dmp, "username", "testuser");
		domainMethodProcessorService.setProperty(dmp, "password", "password");
		return "OK";
	}

}
