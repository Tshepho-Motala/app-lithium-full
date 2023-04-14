package lithium.service.casino.mock.all.controllers;

import lithium.service.casino.client.objects.GenericMockPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
@Slf4j
public class GuiController {
	@RequestMapping(value = "/index")
	public String index(Model model) {
		log.info("Got to the root of the server path");
		return "index";
	}

	@RequestMapping(value = "/")
	public String root(Model model) {
		log.info("Got to the root of the server path");
		model.addAttribute("eventName", "From root");
		return "redirect:index";
	}

	@RequestMapping(value = "/launchGame")
	public String startGame(Model model, final HttpServletRequest request) {
		log.info("Got to the game start url");
		model.addAttribute("actionList", produceActionList());
		model.addAttribute("resendCount", 0);
		model.addAttribute("delayBetweenResendMs", 0);
		model.addAttribute("amountCentsPrimaryAction", 50);
		model.addAttribute("amountCentsSecondaryAction", 50);
		model.addAttribute("mockSessionId", request.getParameter("mockSessionId"));
		model.addAttribute("mockPayload", new GenericMockPayload(Long.parseLong(request.getParameter("mockSessionId")), 0, 0, 50, 10));
		return "actions";
	}

	@RequestMapping(value = "/actions")
	public String actions(Model model, final HttpServletRequest request, @RequestBody MultiValueMap<String, String> formData) {
		model.addAttribute("actionItems", formData.get("actionItems"));

		model.addAttribute("mockSessionId", formData.getFirst("mockSessionId"));

		model.addAttribute("mockPayload", new GenericMockPayload(
				Long.parseLong(formData.getFirst("mockSessionId")),
				Integer.parseInt(formData.getFirst("resendCount")),
				Long.parseLong(formData.getFirst("delayBetweenResendMs")),
				Long.parseLong(formData.getFirst("amountCentsPrimaryAction")),
				Long.parseLong(formData.getFirst("amountCentsSecondaryAction"))));

		model.addAttribute("actionList", produceActionList());
		model.addAttribute("resendCount", formData.getFirst("resendCount"));
		model.addAttribute("delayBetweenResendMs", formData.getFirst("delayBetweenResendMs"));
		model.addAttribute("amountCentsPrimaryAction", formData.getFirst("amountCentsPrimaryAction"));
		model.addAttribute("amountCentsSecondaryAction", formData.getFirst("amountCentsSecondaryAction"));
		return "actions";
	}

	private ArrayList<String> produceActionList() {
		ArrayList<String> actionList = new ArrayList();

		actionList.add("balance");
		actionList.add("validate");
		actionList.add("bet");
		actionList.add("win");
		actionList.add("negativeBet");
		actionList.add("refund");
		actionList.add("freespin");
		actionList.add("freespinFeature");
		actionList.add("betAndWin");
		actionList.add("winAndNegativeBet");
		actionList.add("betAndWinAndRefund");
		actionList.add("winAndRefund");
		actionList.add("freespinFeatureAndRefund");
		actionList.add("freespinAndRefund");
		actionList.add("negativeBetAndRefund");
		actionList.add("winAndNegativeBetAndRefund");

		return actionList;
	}

}