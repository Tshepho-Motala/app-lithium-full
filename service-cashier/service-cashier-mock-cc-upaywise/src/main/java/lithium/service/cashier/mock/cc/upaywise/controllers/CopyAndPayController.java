package lithium.service.cashier.mock.cc.upaywise.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import lithium.config.LithiumConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CopyAndPayController {
	@Autowired
	private LithiumConfigurationProperties config;
	
	@PostMapping("/processCopyAndPayThreed")
	public ModelAndView payment(
		WebRequest webRequest,
		@RequestParam("payId") String payId
	) {
		log.info("PayId = "+payId);
		return new ModelAndView("payment", "transactionId", webRequest.getParameter("t"));
	}
	
	/** results=%20Error%20while%20processing%20transaction
	 * &transid=3058793343020157250
	 * &ecis=null
	 * &trackids=805
	 * &responsecodes=625
	 * &auths=null
	 * &rrns=null
	 * &udfs5=3D%20Secure%20Check%20Failed,%20Cannot%20continue%20transaction%20-%20invalid%20creditcard,%20bank%20account%20number%20or%20bank%20name
	 * &currencys=ZAR
	 * &amounts=13.39%20
	 * &email=riaanscic1@riaan.playsafesa.com
	 * &udfs1=null
	 * &udfs2=null
	 * &udfs3=null
	 * &udfs4=null
	 * @author riaans
	 * @throws IOException 
	 *
	 */
	@PostMapping("/confirmOTP")
	public void confirmOTP(
		@RequestParam("otp") String otp,
		@RequestParam("tranId") String tranId,
		HttpServletResponse response
	) throws IOException {
		log.info("otp = "+otp);
		boolean fail = false;
		if (otp.equalsIgnoreCase(tranId)) fail = true;
		response.sendRedirect(
			config.getGatewayPublicUrl()+
			"/service-cashier-processor-cc-upaywise/callback/e52ba9fda1d019cff025b95dc7d75172/?"+
//			"/service-cashier/external/callback/do/upaywise/d1dc0442ea82c22bdafdf6d89ed99ac4cf987db8?"+
			(fail?"results=%20Error%20while%20processing%20transaction":"results=")+
			"&transid=305879334302015"+otp+
			"&ecis=null"+
			"&trackids="+tranId+
			((otp.equalsIgnoreCase("001"))?"&responsecodes=001":(fail?"&responsecodes=506":"&responsecodes=000"))+
			"&auths=null"+
			"&rrns=null"+
			(fail?"&udfs5=3D%20Secure%20Check%20Failed,%20Cannot%20continue%20transaction%20-%20invalid%20creditcard,%20bank%20account%20number%20or%20bank%20name":"&udfs5=")+
			"&currencys=ZAR"+
			"&amounts=13.39%20"+
			"&email=riaanscic1@riaan.playsafesa.com"+
			"&udfs1=null"+
			"&udfs2=null"+
			"&udfs3=null"+
			"&udfs4=null"
		);
	}
}
