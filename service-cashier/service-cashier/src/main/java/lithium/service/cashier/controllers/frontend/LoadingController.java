package lithium.service.cashier.controllers.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoadingController {
	@RequestMapping("/frontend/loading")
	public String loading() {
		return "loading";
	}
	@RequestMapping("/frontend/loadingrefresh")
	public String loadingRefresh() {
		return "loadingrefresh";
	}
	
	@RequestMapping("/frontend/opencashier")
	public @ResponseBody String openCashier() {
		log.debug("frontend/opencashier");
		return "<html><head><script src='https://code.jquery.com/jquery-3.2.1.min.js' integrity='sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=' crossorigin='anonymous'></script></head><body><script>$(document).ready(function() {console.warn('opencashier');parent.postMessage('lithium_cashier_open', '*'); });</script></body></html>";
	}

	@RequestMapping("/frontend/closepage")
	public String closePage() {
		return "closepage";
	}
}
