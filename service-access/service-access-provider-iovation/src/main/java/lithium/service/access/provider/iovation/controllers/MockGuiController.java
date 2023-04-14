package lithium.service.access.provider.iovation.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class MockGuiController {
	@RequestMapping(value = "/index")
	public String index(Model model) {
		log.info("Got to the root of the server path");
		return "index";
	}

	@RequestMapping(value = "/")
	public String root(Model model) {
		log.info("Got to the root of the server path");
//		model.addAttribute("eventName", "From root");
		return "redirect:index";
	}


//	@RequestMapping(value = "/actions")
//	public String actions(Model model, final HttpServletRequest request, @RequestBody MultiValueMap<String, String> formData) {
//		model.addAttribute("actionItems", formData.get("actionItems"));
//
//		model.addAttribute("mockSessionId", formData.getFirst("mockSessionId"));
//
//		model.addAttribute("mockPayload", new GenericMockPayload(
//				Long.parseLong(formData.getFirst("mockSessionId")),
//				Integer.parseInt(formData.getFirst("resendCount")),
//				Long.parseLong(formData.getFirst("delayBetweenResendMs")),
//				Long.parseLong(formData.getFirst("amountCentsPrimaryAction")),
//				Long.parseLong(formData.getFirst("amountCentsSecondaryAction"))));
//
//		model.addAttribute("actionList", produceActionList());
//		model.addAttribute("resendCount", formData.getFirst("resendCount"));
//		model.addAttribute("delayBetweenResendMs", formData.getFirst("delayBetweenResendMs"));
//		model.addAttribute("amountCentsPrimaryAction", formData.getFirst("amountCentsPrimaryAction"));
//		model.addAttribute("amountCentsSecondaryAction", formData.getFirst("amountCentsSecondaryAction"));
//		return "actions";
//	}

}
