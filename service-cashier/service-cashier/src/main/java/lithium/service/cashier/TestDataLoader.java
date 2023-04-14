//package lithium.service.cashier;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import lithium.service.cashier.client.objects.ProcessorProperty;
//import lithium.service.cashier.data.entities.DomainMethod;
//import lithium.service.cashier.data.entities.DomainMethodProcessor;
//import lithium.service.cashier.data.entities.Method;
//import lithium.service.cashier.data.entities.Processor;
//import lithium.service.cashier.services.DomainMethodProcessorService;
//import lithium.service.cashier.services.DomainMethodService;
//import lithium.service.cashier.services.DomainService;
//import lithium.service.cashier.services.MethodService;
//import lithium.service.cashier.services.ProcessorService;
//
//@Service
//public class TestDataLoader {
//	
//	@Autowired DomainService domainService;
//	@Autowired DomainMethodService domainMethodService;
//	@Autowired DomainMethodProcessorService domainMethodProcessorService;
//	@Autowired MethodService methodService;
//	@Autowired ProcessorService processorService;
//
//	public void loadTestData() throws Exception {
//		domainService.findOrCreateDomain("luckybetz");
//		Method methodcc = methodService.save("service-cashier-method-cc", "service-cashier-method-cc", null, true);
//		DomainMethod dm = domainMethodService.create("Credit Card", null, null, null, true, true, 1, methodcc.getId(), "luckybetz");
//		
//		Processor procQwipi = processorService.save("service-cashier-processor-cc-qwipi", "service-cashier-processor-cc-qwipi", true, true, true, true, true, methodcc, null, null);
//		
//		processorService.saveProperty(procQwipi, 
//				ProcessorProperty.builder().type("string").name("merNo").description("Merchant Number")
//				.defaultValue("MERNO").build());
//
//		processorService.saveProperty(procQwipi, 
//				ProcessorProperty.builder().type("yesno").name("3dsecure").description("Processess using 3DSecure")
//				.defaultValue("yes").build());
//
//		processorService.saveProperty(procQwipi, 
//				ProcessorProperty.builder().type("url").name("paymenturls2s").description("Server to Server Payment URL")
//				.defaultValue("http://localhost:9000/service-cashier-mock-cc-qwipi/universalS2S/payment").build());
//
//		processorService.saveProperty(procQwipi, 
//				ProcessorProperty.builder().type("url").name("paymenturl3ds").description("3D Secure Payment URL")
//				.defaultValue("http://localhost:9000/service-cashier-mock-cc-qwipi/universal3DS/payments.jsp").build());
//		
//		DomainMethodProcessor dmpQwipi1 = domainMethodProcessorService.create(dm, procQwipi, "Qwipi 3DS", true, 50);
//		domainMethodProcessorService.setProperty(dmpQwipi1, "3dsecure", "yes");
//
//		DomainMethodProcessor dmpQwipi2 = domainMethodProcessorService.create(dm, procQwipi, "Qwipi Non3DS", true, 50);
//		domainMethodProcessorService.setProperty(dmpQwipi2, "3dsecure", "no");
//
//	}
//	
//}
