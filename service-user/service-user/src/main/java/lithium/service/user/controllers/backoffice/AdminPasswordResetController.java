package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.user.services.PasswordResetService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/passwordreset")
@Slf4j
public class AdminPasswordResetController {
	@Autowired PasswordResetService passwordResetService;

	@PostMapping("/clearFailedResetCount")
	public Response<Void> clearFailedResetCount(
		@RequestParam("playerGuid") String playerGuid,
		LithiumTokenUtil tokenUtil
	) {
		try {
			passwordResetService.clearResetTokensAndCount(playerGuid, tokenUtil);
			return Response.<Void>builder().status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to clear FailedReset count [playerGuid="+playerGuid+"] "+e.getMessage(), e);
			return Response.<Void>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}
