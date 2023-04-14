package lithium.service.games.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoadingController {
	@RequestMapping("/frontend/closegame")
	public @ResponseBody String closeGame() {
		log.debug("frontend/closegame");
		return "<html><head><script src='https://code.jquery.com/jquery-3.2.1.min.js' integrity='sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=' crossorigin='anonymous'></script></head><body><script>$(document).ready(function() {console.warn('closegame');parent.postMessage('lithium_close_game', '*'); });</script></body></html>";
	}
}