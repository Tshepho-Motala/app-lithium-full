//package lithium.service.access.provider.iovation.controllers;
//
//import static org.apache.commons.collections.MapUtils.isNotEmpty;
//import static org.apache.commons.lang.StringUtils.isNotBlank;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.catchThrowable;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.hamcrest.Matchers.nullValue;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.web.bind.annotation.RequestMethod.GET;
//import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//import java.util.Map.Entry;
//
//import org.assertj.core.api.Assertions;
//import org.hamcrest.Matcher;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import lithium.service.Response;
//import lithium.service.Response.Status;
//import lithium.service.access.client.objects.ExternalAuthorizationRequest;
//import lithium.service.access.provider.iovation.data.CheckTransactionDetails;
//import lithium.service.access.provider.iovation.data.CheckTransactionDetailsResponse;
//import lithium.service.access.provider.iovation.data.Details;
//import lithium.service.access.provider.iovation.data.Device;
//import lithium.service.access.provider.iovation.data.DeviceRegistrationRequest;
//import lithium.service.access.provider.iovation.data.RegistrationResult;
//import lithium.service.access.provider.iovation.test.model.RestCallRequestConfig;
//import static lithium.service.access.provider.iovation.test.utils.RestCallUtils.*;
//
///**
// * @author vuyanindala
// * @since 12 August 2019
// *
// *        The test is configured to disable a lot of the expected behavior of
// *        the project {@link TestPropertySource} overrides the application
// *        properties to point to only properties required for this unit of tests
// *        {@link ContextConfiguration} overrides the application bootstrapping
// *        class {@link WebMvcTest} implements a harness enabling mocking of the
// *        mvc framework {@link AutoConfigureMockMvc} overrides the security, in
// *        this unit of work, we expect that security is assumed
// *
// */
//@TestPropertySource(locations = "classpath:bootstrap-test.properties")
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = TestApplication.class)
//@WebMvcTest
//@AutoConfigureMockMvc(secure = false)
//public class IovationDeviceControllerTests {
//
//	@Autowired
//	private MockMvc mvc;
//
//	@MockBean
//	private IovationController iovationController;
//
//	@MockBean
//	private IovationDeviceManagerController deviceManagerController;
//
//	private static final String USERGUID = "tv_15938";
//	private static final String DEVICEID = "0400UtAq9oNuGZINf94lis1zt";
//	private static final String RULE_NAME = "login";
//	private static final String IP_ADDRESS = "192.0.2.235";
//	private static final String DOMAIN_NAME = "iovation";
//	private final String CONTENT_TYPE = APPLICATION_JSON_VALUE;
//	private final Status lithiumOkStatus = Status.OK;
//	private final String baseEndpoint = "/external/checkAuthorization/";
//
//	@Test
//	public void test_whenAuthorizationUnsuccessful() {
//		Response<CheckTransactionDetailsResponse> response = buildCheckTransactionDetailsDataResponse(USERGUID, null,
//				null, null);
//		response.setStatus(Status.INTERNAL_SERVER_ERROR);
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class)))
//				.thenReturn(response);
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		String message = "Error from remote caller";
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(baseEndpoint).contentType(CONTENT_TYPE).content(stringify(createExternalAuthRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(false)))
//					.andExpect(jsonPath("$.data.message", nullValue()))
//					.andExpect(jsonPath("$.data.errorMessage", notNullValue()))
//					.andExpect(jsonPath("$.data.errorMessage", equalTo(message)));
//
//		});
//
//		assertThat(unforseen).isNull();
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(0)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultAcceptedAndDeviceRegistered() {
//		// make sure that the device does not go through registration process
//		RegistrationResult registrationResult = createRegistrationResult("MATCH");
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Owned Evidence", "A"));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, "OK");
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(0)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//// Both methods are allowed and by restricting it, it broek the service calls from user auth.
////	@Test
////	public void test_tryCallEndpointUsingIncorrectHttpMethod_throwException() {
////		// make sure that the device does not go through registration process
////		RegistrationResult registrationResult = createRegistrationResult("MATCH");
////
////		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
////				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Owned Evidence", "A"));
////		when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
////
////		Throwable unforseen = catchThrowable(() -> {
////			RestCallRequestConfig config = configRequest(true, "OK");
////			config.setMethod(PATCH);
////
////			ResultActions actionResults = mvc.perform(prepareRestCallByMethod(config));
////			actionResults.andExpect(status().is4xxClientError());
////			actionResults.andExpect(status().isMethodNotAllowed());
////			actionResults.andExpect(header().string("Allow", "GET"));
////		});
////
////		assertThat(unforseen).isNull();
////
////		verify(iovationController, times(0)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
////		verify(deviceManagerController, times(0)).registerDevice("", any(DeviceRegistrationRequest.class));
////	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultAcceptedAndDeviceNoneRegistered() {
//		RegistrationResult registrationResult = createRegistrationResult("NONE_REGISTERED");
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Owned Evidence", "A"));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, "OK");
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultAcceptedAndDeviceNoMatch() {
//		RegistrationResult registrationResult = createRegistrationResult("NO_MATCH");
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Owned Evidence", "A"));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, "OK");
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultAcceptedAndRegistationNull() {
//		RegistrationResult registrationResult = null;
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Owned Evidence", "A"));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, "OK");
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultDenied() {
//		RegistrationResult registrationResult = createRegistrationResult("NONE_REGISTERED");
//
//		String failureReason = "Banned Device";
//		String txResult = "D"; // make sure that the device does not go through registration process
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, failureReason, txResult));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(false, failureReason);
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.message", nullValue()))
//					.andExpect(jsonPath("$.data.errorMessage", notNullValue()))
//					.andExpect(jsonPath("$.data.errorMessage", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(0)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultReviewAndNoMatch() {
//		RegistrationResult registrationResult = createRegistrationResult("NO_MATCH");
//
//		String reviewReason = "Too many registered devices";
//		String txResult = "R";
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, reviewReason, txResult));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, reviewReason);
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk())
//					.andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultReviewAndNoneRegistered() {
//		RegistrationResult registrationResult = createRegistrationResult("NONE_REGISTERED");
//
//		String reviewReason = "Missing documentation";
//		String txResult = "R";
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, reviewReason, txResult));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, reviewReason);
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk())
//					.andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void test_whenAuthorizationSuccessful_resultReviewAndRegistationNull() {
//		RegistrationResult registrationResult = null;
//
//		when(iovationController.checkTransactionDetails(anyString(), any(CheckTransactionDetails.class))).thenReturn(
//				buildCheckTransactionDetailsDataResponse(USERGUID, registrationResult, "Device operating system too old", "R"));
//		try {
//			when(deviceManagerController.registerDevice("", any(DeviceRegistrationRequest.class))).thenReturn(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		RestCallRequestConfig config = configRequest(true, "Device operating system too old");
//
//		Throwable unforseen = catchThrowable(() -> {
//
//			mvc.perform(get(config.getEndpoint()).contentType(config.getContentType()).content(stringify(config.getRequest())))
//					.andExpect(status().isOk()).andExpect(jsonPath("$.data", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", notNullValue()))
//					.andExpect(jsonPath("$.data.successful", equalTo(config.isLithiumSuccess())))
//					.andExpect(jsonPath("$.data.errorMessage", nullValue()))
//					.andExpect(jsonPath("$.data.message", notNullValue()))
//					.andExpect(jsonPath("$.data.message", equalTo(config.getExpectedMessage())));
//
//		});
//
//		assertThat(unforseen).isNull();
//
//
//		verify(iovationController, times(1)).checkTransactionDetails(anyString(), any(CheckTransactionDetails.class));
//		try {
//			verify(deviceManagerController, times(1)).registerDevice("", any(DeviceRegistrationRequest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	//	PRIVATE HELPER METHODS //
//
//	/**
//	 * Build a {@link CheckTransactionDetailsResponse} object expected in the test
//	 * response result
//	 *
//	 * @param userId             - account code
//	 * @param registrationResult - device registration result object
//	 * @param reason             - transaction result reason
//	 * @param result             - transaction result status
//	 *
//	 * @return
//	 */
//	private Response<CheckTransactionDetailsResponse> buildCheckTransactionDetailsDataResponse(String userId,
//			RegistrationResult registrationResult, String reason, String result) {
//
//		CheckTransactionDetailsResponse data = new CheckTransactionDetailsResponse();
//		data.setAccountCode(userId);
//
//		Device device = new Device();
//		device.setRegistrationResult(registrationResult);
//
//		Details details = new Details();
//		details.setDevice(device);
//
//		data.setDetails(details);
//		data.setId(UUID.randomUUID());
//		data.setReason(reason);
//		data.setResult(result);
//		data.setTrackingNumber(data.getId().getLeastSignificantBits());
//
//		return Response.<CheckTransactionDetailsResponse>builder().data(data).status(lithiumOkStatus).build();
//	}
//
//	/**
//	 * Creates a {@link ExternalAuthorizationRequest} with static field values
//	 *
//	 * @return
//	 */
//	private ExternalAuthorizationRequest createExternalAuthRequest() {
//		return ExternalAuthorizationRequest.builder()
//		.userGuid(USERGUID)
//		.deviceId(DEVICEID)
//		.ruleName(RULE_NAME)
//		.ip(IP_ADDRESS)
//		.domainName(DOMAIN_NAME)
//		.build();
//	}
//
//	/**
//	 * Creates a {@link DeviceRegistrationRequest} with sample data provided in the iovation spec
//	 * @return
//	 */
//	private DeviceRegistrationRequest createDeviceRegisterRequest() {
//		return DeviceRegistrationRequest.builder()
//		.blackBox("0400mTR4Z7+BQcwNf94lis1ztoi7MenWI6wSPpYSsN2wZsGy41+oHFEMUb3DSv4rtPRsaU5Om4xOT+M\n" +
//				"		h7zj6gzKmCAyCiDVa0KzS3EXoW3wRrlco8IG1qk9lAA8PVjCHcdlxDXMzdeKt5csId7H9j3oUV0VWmvE\n" +
//				"		qu1A58cFCb3XpK0K8hZCSHGBUGQbz44XiMf/eY92hNSmr+k28EssPaX5qsyDqFa/NZMpWsBx6w1tx0mM\n" +
//				"		Zz7HtmSQrKuHt39bvxE86eVaYJEF00M2dmwpFJMf4g6rpu+l5pUzVzKEvln1Nr5VHuaiVCo5mClSAFu7\n" +
//				"		Fz+AKvajloP/UZ1tHAt9MuhX8TIaJpdrPQvasBjlBvlffoqpP2GXIlrdzeNWV2gQ=")
//		.userAccountCode("mylogin@yoursite")
//		.subscriberId("1000")
//		.userIPAddress("111.222.101.202")
//		.build();
//	}
//
//	/**
//	 * Basic configuration that configures only the expected message and lithium
//	 * result
//	 *
//	 * @param lithiumStatus - indicates whether we expect lithium call to return
//	 *                      succesful or not
//	 * @param message       - the message (data message) we expect from lithium
//	 * @return
//	 */
//	private RestCallRequestConfig configRequest(boolean lithiumStatus, String message) {
//		RestCallRequestConfig config = new RestCallRequestConfig();
//		config.setContentType(CONTENT_TYPE);
//		config.setEndpoint(baseEndpoint);
//		config.setMethod(GET);
//		config.setRequest(createExternalAuthRequest());
//		config.setLithiumSuccess(lithiumStatus);
//		config.setExpectedMessage(message);
//		return config;
//	}
//
//	/**
//	 * Creates a basic {@link RegistrationResult} result
//	 *
//	 * @param matchStatus - the registration result status to be returned
//	 *
//	 * @return
//	 */
//	private RegistrationResult createRegistrationResult(String matchStatus) {
//		RegistrationResult registrationResult = new RegistrationResult();
//		registrationResult.setMatchStatus(matchStatus);
//
//		return registrationResult;
//	}
//
//}
