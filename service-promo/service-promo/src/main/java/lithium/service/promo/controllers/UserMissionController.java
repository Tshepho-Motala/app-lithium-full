package lithium.service.promo.controllers;

import lithium.service.promo.data.entities.UserPromotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.services.UserPromotionService;
import lithium.service.promo.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/promotions")
public class UserMissionController {
	@Autowired
	UserPromotionService userPromotionService;
	@Autowired UserService userService;
	
	@GetMapping("/user")
	public Response<UserPromotion> um(
		@RequestParam(name="guid", required=true) String playerGuid
	) throws Exception {
		User user = userService.findOrCreate(playerGuid);
		log.debug("UserPromotion for : "+user);
		
		UserPromotion userPromotion = userPromotionService.findActiveByUser(user);
		
		log.debug("UserPromotion :"+ userPromotion);
		
//		userPromotion = userMissionService.refreshUserMission(userPromotion);
		
		return Response.<UserPromotion>builder().data(userPromotion).status(Status.OK).build();
	}
}
