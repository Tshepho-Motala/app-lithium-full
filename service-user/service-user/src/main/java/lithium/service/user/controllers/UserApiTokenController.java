package lithium.service.user.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.services.UserApiTokenService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/userapi")
public class UserApiTokenController {
	@Autowired UserApiTokenService userApiTokenService;
	@Autowired UserService userService;
	
	@Autowired private ModelMapper mapper;

	@RequestMapping("/getApiTokenIfValid")
	public Response<lithium.service.user.client.objects.UserApiToken> getApiTokenIfValid(
		@RequestParam("guid") String guid,
		@RequestParam("apiToken") String apiToken
	) {
		UserApiToken token = userApiTokenService.getApiTokenIfValid(guid, apiToken);
		
		if(token != null) {
			return Response.<lithium.service.user.client.objects.UserApiToken>builder()
					.data(mapper.map(
							token, 
							lithium.service.user.client.objects.UserApiToken.class))
					.status(Status.OK)
					.build();
		}
		
		return Response.<lithium.service.user.client.objects.UserApiToken>builder()
				.status(Status.INVALID_DATA)
				.build();
	}
	
	@RequestMapping("/saveApiToken")
	public Response<lithium.service.user.client.objects.UserApiToken> saveApiToken(
		@RequestParam("guid") String guid,
		@RequestParam("apiToken") String apiToken
	) {
		return Response.<lithium.service.user.client.objects.UserApiToken>builder()
				.data(mapper.map(
						userApiTokenService.saveApiToken(guid, apiToken), 
						lithium.service.user.client.objects.UserApiToken.class))
				.status(Status.OK)
				.build();
	}
	
	@RequestMapping("/getUser")
	public Response<lithium.service.user.client.objects.User> getUser(
		@RequestParam("guid") String guid,
		@RequestParam(name="apiToken", required=false) String apiToken
	) {
		UserApiToken token = userApiTokenService.getApiTokenIfValid(guid, apiToken);
		if (token != null || apiToken == null) {
			User user = userService.findFromGuid(guid);
			
			if (user != null) {
				lithium.service.user.client.objects.User resultUser = userService.convert(user);

				if (user.getCurrent() != null) {
					List<UserRevisionLabelValue> lvList = user.getCurrent().getLabelValueList();
					if (lvList != null && !lvList.isEmpty()) {
						Map<String, String> lvMap = new HashMap<>();
						for (UserRevisionLabelValue lv : lvList) {
							lvMap.put(lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
						}
						resultUser.setLabelAndValue(lvMap);
					}
				}
				
				if (apiToken != null) resultUser.setApiToken(token.getToken());

				return Response.<lithium.service.user.client.objects.User>builder()
					.data(resultUser)
					.status(Status.OK)
					.build();
			}
		}
		
		return Response.<lithium.service.user.client.objects.User>builder()
				.status(Status.INVALID_DATA)
				.build();
	}
	
	@RequestMapping("/getUserByApiToken")
	public Response<lithium.service.user.client.objects.User> getUserByApiToken(
		@RequestParam(name="apiToken") String apiToken) {
		
		UserApiToken token = userApiTokenService.findByToken(apiToken);
		
		if(token != null) {
			return getUser(token.getGuid(), token.getToken());
		}

		return Response.<lithium.service.user.client.objects.User>builder()
				.status(Status.INVALID_DATA)
				.build();
	}

	/**
	 * Used to retrieve a short guid for the user.
	 * This is also used as the referral code when performing a social referral.
	 * @param shortGuid
	 * @return
	 */
	@RequestMapping("/getUserGuidByShortGuid")
	public Response<String> getUserGuidByShortGuid(
			@RequestParam(name="shortGuid") String shortGuid) {

		UserApiToken token = userApiTokenService.findByShortGuid(shortGuid);

		if(token != null) {
			return Response.<String>builder().data(token.getGuid()).status(Status.OK).build();
		}

		return Response.<String>builder()
				.status(Status.INVALID_DATA)
				.build();
	}

	@RequestMapping("/getApiTokenByUserGuid")
	public Response<lithium.service.user.client.objects.UserApiToken> getApiTokenByUserGuid(
			@RequestParam(name="guid") String guid) {

		UserApiToken token = userApiTokenService.findByGuid(guid);

		if(token != null) {
			return Response.<lithium.service.user.client.objects.UserApiToken>builder()
					.data(mapper.map(token, lithium.service.user.client.objects.UserApiToken.class))
					.status(Status.OK)
					.build();
		}

		return Response.<lithium.service.user.client.objects.UserApiToken>builder()
				.status(Status.INVALID_DATA)
				.build();
	}
}
