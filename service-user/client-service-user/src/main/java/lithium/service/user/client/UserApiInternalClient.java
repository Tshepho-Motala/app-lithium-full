package lithium.service.user.client;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.DuplicateCheckRequestData;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdateBasic;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.objects.EcosystemUserProfiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(name="service-user")
public interface UserApiInternalClient {

	@RequestMapping("/userapiinternal/users/list/findByGuid")
	public Response<List<User>> getUsers(@RequestParam("guids") List<String> guids);
	
	@RequestMapping("/userapiinternal/users/findByGuid")
	public Response<User> getUser(@RequestParam("guid") String guid);

	@RequestMapping("/userapiinternal/get-user-by-email")
	public Response<User> getUserByEmail(
			@RequestParam("domainName") String domainName,
			@RequestParam("email") String email
	);

	/** See lithium.service.user.controllers.UserApiInternalController#getUserByCellphoneNumber */
	@RequestMapping("/userapiinternal/getUserByCellphoneNumber")
	public Response<User> getUserByCellphoneNumber(
			@RequestParam("domainName") String domainName,
			@RequestParam("cellphoneNumber") String cellphoneNumber
	);

	@RequestMapping(value = "/userapiinternal/user", method = RequestMethod.POST, produces="application/json", consumes="application/json")
//	public Response<User> updateUser(User user);
	public Response<User> updateUser(Map<String, String> map);

    @RequestMapping("/userapiinternal/system/getByUserId")
    public Response<User> getUserById(@RequestParam("id") Long id);

	@RequestMapping(method=RequestMethod.POST, value="/userapiinternal/user/markHasSelfExcludedAndOptOutComms")
	public User markHasSelfExcludedAndOptOutComms(@RequestParam("guid") String guid) throws UserNotFoundException;

	@RequestMapping(method = RequestMethod.POST, value = "/userapiinternal/user/saveverificationstatus")
	public Response<User> editUserVerificationStatus(@RequestBody UserVerificationStatusUpdate statusUpdate) throws UserClientServiceFactoryException;

	@RequestMapping(method=RequestMethod.POST, value="/system/account-status/change")
	public User changeAccountStatus(@RequestBody UserAccountStatusUpdate statusUpdate);

	@RequestMapping(method=RequestMethod.POST, value="/system/account-status/change-basic")
	public Response<Boolean> changeAccountStatusBasic(@RequestBody UserAccountStatusUpdateBasic statusUpdate);

	@RequestMapping(method=RequestMethod.POST, value="/system/user/update-protection-of-customer-funds-version")
	public User updateProtectionOfCustomerFundsVersion(@RequestParam("userGuid") String userGuid)
		throws Status500InternalServerErrorException;

	@RequestMapping(method = RequestMethod.GET, value = "/system/user/additionaldata")
	public Response<Map<String, String>> findUserLabelValues(@RequestParam("userGuid") String userGuid) throws UserNotFoundException;

	@RequestMapping(method = RequestMethod.POST, value = "/system/user/additionaldata")
	public Response<String> updateOrAddUserLabelValues(@RequestParam("userGuid") String userGuid, @RequestBody Map<String, String> additionalData) throws UserNotFoundException;

	@RequestMapping(method = RequestMethod.POST, value ="/system/players/{domainName}/incompleteuser/additionaldata")
	public Response<String> updateOrAddIncompleteUserLabelValues(@PathVariable("domainName") String domainName, @RequestParam("email") String email, @RequestBody Map<String, String> additionalData) throws UserNotFoundException;

	@RequestMapping(method = RequestMethod.GET, value = "/system/players/{domainName}/incompleteuser/additionaldata")
	public Response<Map<String, String>> findIncompleteUserLabelValues(@PathVariable("domainName") String domainName, @RequestParam("email") String email) throws UserNotFoundException;

	@RequestMapping(method = RequestMethod.POST, value = "/system/user/validate-session")
	public void validateSession(@RequestParam("domainName") String domainName,
	    @RequestParam("loginEventId") Long loginEventId) throws Status401UnAuthorisedException;

	@RequestMapping(method = RequestMethod.POST, value = "/system/user/validate-and-update-session")
	public void validateAndUpdateSession(@RequestParam("domainName") String domainName,
		@RequestParam("loginEventId") Long loginEventId) throws Status401UnAuthorisedException;

	@RequestMapping(method = RequestMethod.POST, value = "/system/user/logout")
	public void logout(@RequestParam("userGuid") String userGuid);

	@RequestMapping(method = RequestMethod.GET, value = "/userapiinternal/user/block-iban-mismatch-user")
	void blockIBANMismatchUser(@RequestParam("userGuid") String userGuid,
	                           @RequestParam("accoutNoteMessage") String accoutNoteMessage,
	                           @RequestParam("financeNoteMessage") String financeNoteMessage);

	@RequestMapping(method = RequestMethod.POST, path = "/system/user/save-verification-status")
	public Response<User> updateVerificationStatus(@RequestParam("forceUpdate") boolean forceUpdate, @RequestBody @Valid UserVerificationStatusUpdate userVerificationStatusUpdate) throws Exception;

	@RequestMapping(method = RequestMethod.PUT, path = "/system/user/biometrics-status")
	public Response<User> updateBiometricsStatus(@RequestBody @Valid UserBiometricsStatusUpdate userBiometricsStatusUpdate) throws Exception;

	@RequestMapping(method = RequestMethod.POST, path = "/system/user/{id}/test-account/{isTestAccount}")
	public Response<User> setTest(@PathVariable("id") Long id, @PathVariable("isTestAccount") boolean isTestAccount) throws Exception;

	@RequestMapping(method = RequestMethod.POST, path = "/system/user/{id}/tag/add")
	public Response<User> categoryAddPlayer(@PathVariable("id") Long id, @RequestParam(name="tagIds") List<Long> tagIds) throws Exception;

	@RequestMapping(method = RequestMethod.DELETE, path = "/system/user/{id}/tag/remove")
	public Response<User> categoryRemovePlayer(@PathVariable("id") Long id, @RequestParam(name="tagIds") List<Long> tagIds) throws Exception;

	@RequestMapping(method = RequestMethod.DELETE, path = "/system/user/{id}/tag/remove/all")
	public Response<User> categoryRemoveAllPlayer(@PathVariable("id") Long id) throws Exception;

	@RequestMapping(path = "/system/user/{id}/save-promotions-out-out", method = RequestMethod.POST)
	Response<User> setPromotionsOptOut(@PathVariable("id") Long id, @RequestParam("optOut") boolean optOut);

	@RequestMapping(value = "/system/user/find-userguids-with-birthdays-today", method = RequestMethod.POST)
	SimplePageImpl<String> getUserGuidsWhosBirthdayIsToday(@RequestParam(name="page", required = false, defaultValue = "0") int page, @RequestParam(name="limit", required = false, defaultValue = "1000") int limit, @RequestBody List<String> guids);

	@RequestMapping(value = "/system/user/find-user-duplicates", method = RequestMethod.POST)
	public Response<List<User>> findDuplicateUsers(
			@RequestBody DuplicateCheckRequestData data);

	@RequestMapping(method = RequestMethod.GET, value = "/system/user/get-ecosystem-user-profile")
	public Response<EcosystemUserProfiles> getEcosystemUserProfile(@RequestParam("id") Long userId);

	@RequestMapping(method = RequestMethod.GET, value = "/system/user/ecosystem/linked-user-guid/{relationship-type}")
	public Response<User> getLinkedEcosystemUserGuid(@RequestParam("userGuid") String userGuid, @PathVariable("relationship-type") EcosystemRelationshipTypes relationshipType);

	@RequestMapping(value = "/system/user/find-user-by-username-then-email-then-cell", method = RequestMethod.GET)
	public Response<User> findByUsernameThenEmailThenCell(@RequestParam("domainName") String domainName,
														  @RequestParam("UsernameEmailOrCell") String usernameEmailOrCell);
}
