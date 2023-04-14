package lithium.service.pushmsg.provider.onesignal;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lithium.service.pushmsg.client.internal.DeviceEditRequest;
import lithium.service.pushmsg.client.internal.DeviceEditResponse;
import lithium.service.pushmsg.client.internal.DeviceRequest;
import lithium.service.pushmsg.client.internal.DeviceResponse;
import lithium.service.pushmsg.client.internal.DoProviderRequest;
import lithium.service.pushmsg.client.internal.DoProviderResponse;
import lithium.service.pushmsg.provider.DoProviderInterface;
import lithium.service.pushmsg.provider.onesignal.data.CreateNotification;
import lithium.service.pushmsg.provider.onesignal.data.CreateNotificationResponse;
import lithium.service.pushmsg.provider.onesignal.data.EditDevice;
import lithium.service.pushmsg.provider.onesignal.data.EditDeviceResponse;
import lithium.service.pushmsg.provider.onesignal.data.ViewDevice;
import lithium.service.pushmsg.provider.onesignal.data.ViewDeviceResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoProvider implements DoProviderInterface {
	
	@Override
	public DeviceEditResponse editDevice(DeviceEditRequest request, RestTemplate restTemplate) throws Exception {
		log.debug("DeviceEditRequest :: "+request);
		DeviceEditResponse der = DeviceEditResponse.builder().build();
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", "Basic "+request.getProperty("restApiKey"));
		
		EditDevice ed = EditDevice.builder().build();
		BeanUtils.copyProperties(request, ed);
		
		HttpEntity<EditDevice> httpEntity = new HttpEntity<EditDevice>(ed, headers);
		
		try {
			ResponseEntity<EditDeviceResponse> response = restTemplate.exchange(
				request.getProperty("baseUrl")+"players/"+ed.getUuid(),
				HttpMethod.PUT,
				httpEntity,
				EditDeviceResponse.class
			);
			
			der.setSuccess(response.getBody().getSuccess());
			
			log.debug("Edit Device Response for: "+ed+" response: " + response);
		} catch (Exception ex) {
			log.error("Problem with onesignal sending of pushmsg.", ex);
			throw ex;
		}
		
		return der;
	}

	@Override
	public DeviceResponse deviceInfo(DeviceRequest request, RestTemplate restTemplate) throws Exception {
		log.debug("DeviceRequest :: "+request);
		DeviceResponse dr = DeviceResponse.builder().build();
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", "Basic "+request.getProperty("restApiKey"));
		
		ViewDevice vd = ViewDevice.builder()
		.appId(request.getProperty("appId"))
		.build();
		
		HttpEntity<ViewDevice> httpEntity = new HttpEntity<ViewDevice>(vd, headers);
		
		try {
			ViewDeviceResponse vdr = restTemplate.getForObject(
				request.getProperty("baseUrl")+"players/"+request.getUuid(),
				ViewDeviceResponse.class,
				httpEntity
			);
			
			log.debug("ViewDeviceResponse :: "+vdr);
			
			if (vdr.getCreatedAt() != null) dr.setCreatedAt(new Date(vdr.getCreatedAt()));
			dr.setDeviceModel(vdr.getDeviceModel());
			dr.setDeviceOs(vdr.getDeviceOs());
			dr.setDeviceType(vdr.getDeviceType());
			dr.setIp(vdr.getIp());
			if (vdr.getLastActive() != null) dr.setLastActive(new Date(vdr.getLastActive()));
			dr.setSessionCount(vdr.getSessionCount());
			
			log.debug("View Device Response for: "+vd+" response: " + vdr);
		} catch (Exception ex) {
			log.error("Problem with onesignal sending of pushmsg.", ex);
			throw ex;
		}
		
		return dr;
	}

	@Override
	public DoProviderResponse send(DoProviderRequest request, RestTemplate restTemplate) throws Exception {
		DoProviderResponse response = new DoProviderResponse();
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", "Basic "+request.getProperty("restApiKey"));
		
		CreateNotification cn = CreateNotification.builder()
		.appId(request.getProperty("appId"))
		.includePlayerIds(request.getIncludePlayerIds())
		.templateId(request.getTemplateId())
		.placeholders(request.getPlaceholders())
		.build();
		
		HttpEntity<CreateNotification> httpEntity = new HttpEntity<CreateNotification>(cn, headers);
		
		try {
			CreateNotificationResponse createNotificationResponse = restTemplate.postForObject(request.getProperty("baseUrl")+"notifications", httpEntity, CreateNotificationResponse.class);
			
			log.debug("Create Notification Response for: "+cn+" response: " + createNotificationResponse);
			if (createNotificationResponse.getErrors() == null) {
				response.setProviderId(createNotificationResponse.getId());
				response.setProviderRecipients(""+createNotificationResponse.getRecipients());
				response.setProviderExternalId(createNotificationResponse.getExternalId());
				response.setFailed(false);
			} else {
				response.setFailed(true);
				response.setMessage(createNotificationResponse.getErrors().toString());
			}
			
			return response;
		} catch (Exception ex) {
			log.error("Problem with onesignal sending of pushmsg.", ex);
			throw ex;
		}
	}
}