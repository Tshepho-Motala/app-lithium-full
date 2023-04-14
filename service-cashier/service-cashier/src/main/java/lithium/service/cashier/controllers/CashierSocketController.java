//package lithium.service.cashier.controllers;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Controller
//@RequestMapping("/cashier")
//public class CashierSocketController {
//	
//	@MessageMapping("/test")
//	@SendTo("/cashier/providers")
//	public String test(String test) {
//		log.info("test : "+test);
//		return "Returning : "+test;
//	}
//}