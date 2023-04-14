package lithium.service.access.client;

import eu.bitwalker.useragentutils.UserAgent;
import lithium.service.Response;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.access.client.objects.List;
import lithium.service.access.client.objects.Value;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.geo.client.GeoClient;
import lithium.service.geo.client.objects.Location;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class AccessService {
	@Autowired LithiumServiceClientFactory services;

	public static final String MAP_IP = "ipAddress";
	public static final String MAP_COUNTRY = "country";
	public static final String MAP_COUNTRY_CODE = "countryCode";
	public static final String MAP_CLAIMED_COUNTRY = "claimedCountry";
	public static final String MAP_STATE = "state";
	public static final String MAP_CLAIMED_STATE = "claimedState";
	public static final String MAP_CITY = "city";
	public static final String MAP_CLAIMED_CITY = "claimedCity";
	public static final String MAP_BROWSER = "browser";
	public static final String MAP_OS = "os";
	public static final String MAP_USERAGENT = "userAgent";
	public static final String MAP_POST = "post";
	
	public Map<String, String> parseIpAndUserAgent(String ipAddress, String userAgent) {
		Map<String, String> data = new LinkedHashMap<String, String>();
		data.put(MAP_USERAGENT, userAgent);
		if (ipAddress.contains(",")) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
		}
		data.put(MAP_IP, ipAddress);
		Location location = null;
		try {
			GeoClient geoClient = services.target(GeoClient.class, "service-geo", true);
			Response<Location> response = geoClient.location(ipAddress);
			if (response.isSuccessful()) {
				location = response.getData();
				log.debug("location " + location);
			}
		} catch (Exception e) {
			log.error("Problem getting geo data. " + e.getMessage(), e);
		}
		if (location != null) {
			data.put(MAP_COUNTRY, (location.getCountry() != null)? location.getCountry().getName(): null);
			data.put(MAP_STATE, (location.getLevel1() != null)? location.getLevel1().getName(): null);
			data.put(MAP_CITY, (location.getCity() != null)? location.getCity().getName(): null);
			data.put(MAP_COUNTRY_CODE, location.getCountry() != null ? location.getCountry().getCode() : null);
		}
		// TODO: although this works perfectly, this user agent parsing library used has reached end of life
		//		 - looking for an alternative solution
		UserAgent objUserAgent = null;
		if (userAgent != null) {
			objUserAgent = UserAgent.parseUserAgentString(userAgent);
		}
		if (objUserAgent != null) {
			data.put(MAP_OS, (objUserAgent.getOperatingSystem() != null)? objUserAgent.getOperatingSystem().getName(): null);
			data.put(MAP_BROWSER, (objUserAgent.getBrowser() != null)? objUserAgent.getBrowser().getName(): null);
		}
		return data;
	}
	
	public void addValueToList(String domainName, String listName, String value) {
		try {
			ListClient listClient = services.target(ListClient.class, "service-access", true);
			Response<List> findListByName = listClient.findByDomainNameAndName(domainName, listName);
			log.debug("findListByName : "+findListByName);
			if (findListByName.isSuccessful()) {
				if (findListByName.getData() != null) {
					List list = findListByName.getData();
					log.info("Adding : '"+value+"' to : '"+listName+"' for domain : "+domainName);
					listClient.addListValue(list.getId(), value);
				}
			}
		} catch (Exception e) {
			log.error("Could not add : '"+value+"' to : '"+listName+"' for domain : "+domainName, e);
		}
	}

	public void removeValueFromList(String domainName, String listName, String value) {
		try {
			ListClient listClient = services.target(ListClient.class, "service-access", true);
			Response<List> findListByName = listClient.findByDomainNameAndName(domainName, listName);
			log.debug("findListByName : "+findListByName);
			if (findListByName.isSuccessful()) {
				if (findListByName.getData() != null) {
					List list = findListByName.getData();
					log.info("Removing : '"+value+"' from : '"+listName+"' for domain : "+domainName);
					listClient.removeListDataValue(list.getId(), value);
				}
			}
		} catch (Exception e) {
			log.error("Could not remove : '"+value+"' from : '"+listName+"' for domain : "+domainName, e);
		}
	}

		public AuthorizationResult checkAuthorization(
		String domainName,
		String accessRuleName,
		Map<String,
		String> claimedGeoData,
		Map<String, String> ipAndUserAgentData
	) throws Status551ServiceAccessClientException {
		return checkAuthorization(domainName, accessRuleName, claimedGeoData, ipAndUserAgentData, null, null, false);
	}

	public AuthorizationResult checkAuthorization(
			String domainName,
			String accessRuleName,
			Map<String, String> claimedGeoData,
			Map<String, String> ipAndUserAgentData,
			String deviceId,
			String userGuid,
			boolean overrideValidateOnce
	) throws Status551ServiceAccessClientException {
		return checkAuthorization(domainName, accessRuleName, claimedGeoData, ipAndUserAgentData, deviceId, userGuid, overrideValidateOnce, null, null);
	}

	public AuthorizationResult checkAuthorization(
			String domainName,
			String accessRuleName,
			Map<String, String> claimedGeoData,
			Map<String, String> ipAndUserAgentData,
			String deviceId,
			String userGuid,
			boolean overrideValidateOnce,
			PlayerBasic playerBasic
	) throws Status551ServiceAccessClientException {
		return checkAuthorization(domainName, accessRuleName, claimedGeoData, ipAndUserAgentData, deviceId, userGuid,
				overrideValidateOnce, playerBasic, null);
	}

	public AuthorizationResult checkAuthorization(
			String domainName,
			String accessRuleName,
			Map<String, String> claimedGeoData,
			Map<String, String> ipAndUserAgentData,
			String deviceId,
			String userGuid,
			boolean overrideValidateOnce,
			PlayerBasic playerBasic,
			Map<String, String> additionalData
	) throws Status551ServiceAccessClientException {
		String	claimedCountry = null,
				claimedState = null,
				claimedCity = null,
		claimedPostCode = null;
		if (claimedGeoData != null) {
			claimedCountry = (claimedGeoData.get(MAP_CLAIMED_COUNTRY) != null)? claimedGeoData.get(MAP_CLAIMED_COUNTRY) : null;
			claimedState = (claimedGeoData.get(MAP_CLAIMED_STATE) != null)? claimedGeoData.get(MAP_CLAIMED_STATE) : null;
			claimedCity = (claimedGeoData.get(MAP_CLAIMED_CITY) != null)? claimedGeoData.get(MAP_CLAIMED_CITY) : null;
			claimedPostCode = (claimedGeoData.get(MAP_POST) != null)? claimedGeoData.get(MAP_POST) : null;
		}
		AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
				.ipAddress((ipAndUserAgentData.get(MAP_IP) != null)? ipAndUserAgentData.get(MAP_IP): null)
				.country((ipAndUserAgentData.get(MAP_COUNTRY) != null)? ipAndUserAgentData.get(MAP_COUNTRY): null)
				.claimedCountry((claimedCountry != null && !claimedCountry.isEmpty())? claimedCountry: null)
				.state((ipAndUserAgentData.get(MAP_STATE) != null)? ipAndUserAgentData.get(MAP_STATE): null)
				.claimedState((claimedState != null && !claimedState.isEmpty())? claimedState: null)
				.city((ipAndUserAgentData.get(MAP_CITY) != null)? ipAndUserAgentData.get(MAP_CITY): null)
				.claimedCity((claimedCity != null && !claimedCity.isEmpty())? claimedCity: null)
				.os((ipAndUserAgentData.get(MAP_OS) != null)? ipAndUserAgentData.get(MAP_OS): null)
				.browser((ipAndUserAgentData.get(MAP_BROWSER) != null)? ipAndUserAgentData.get(MAP_BROWSER): null)
				.postCode((claimedPostCode != null && !claimedPostCode.isEmpty())? claimedPostCode: null)
				.deviceId(deviceId)
				.userGuid(userGuid)
				.overrideValidateOnce(overrideValidateOnce)
				.playerBasic(playerBasic)
				.additionalData(additionalData)
				.build();
		AuthorizationClient authorizationClient = null;
		try {
			authorizationClient = services.target(AuthorizationClient.class, "service-access", true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status551ServiceAccessClientException(e);
		}
		Response<AuthorizationResult> authorizationResponse = authorizationClient.checkAuthorization(domainName, accessRuleName, authorizationRequest);
		return (authorizationResponse.getData() != null)? authorizationResponse.getData(): null;
	}

	public boolean isAccessRuleEnabled(String domainName, String accessRuleName, PlayerBasic playerBasic) throws Status551ServiceAccessClientException {
		AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
				.playerBasic(playerBasic)
				.build();
		AuthorizationClient authorizationClient = null;
		try {
			authorizationClient = services.target(AuthorizationClient.class, "service-access", true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status551ServiceAccessClientException(e);
		}
		Response<Boolean> accessRuleEnabledResponse = authorizationClient.isAccessRuleEnabled(domainName, accessRuleName, authorizationRequest);
		return accessRuleEnabledResponse.getData() != null ? accessRuleEnabledResponse.getData() : false;
	}

    public boolean isIpEnabledForDomainNameAndListName(String ipAddress, String domainName, String listName) throws Status551ServiceAccessClientException {
        ListClient listClient;
        try {
            listClient = services.target(ListClient.class, "service-access", true);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status551ServiceAccessClientException(e);
        }
	    Response<List> findListByName = listClient.findByDomainNameAndName(domainName, listName);
        if (findListByName.isSuccessful()) {
            if (findListByName.getData() != null) {
                for (Value value : findListByName.getData().getValues()) {
                    if((ipAddress).contains(value.getData())) {
                        log.debug("IpAddress "+ipAddress+" at list "+listName+" approved for domain="+domainName);
                        return true;
                    }
                }
            }
        }
        log.warn("IpAddress "+ipAddress+" not found at Access List "+listName+", domain="+domainName);
        return false;
    }

    @AllArgsConstructor(access= AccessLevel.PRIVATE)
	public enum ListType implements Serializable {
		IP_LIST("IP_List", "IP List", "List of IP's"),
		IP_RANGE("IP_Range", "IP Range", "Range of IP's"),
		COUNTRY_LIST("Country_List", "Country List", "List of Countries"),
		COUNTRY_LIST_PROFILE("country_list_profile", "Country List (via Profile)", "List of Countries (via Profile)"),
		STATE_LIST("State_List", "State List", "List of States"),
		STATE_LIST_PROFILE("state_list_profile", "State List (via Profile)", "List of States (via Profile)"),
		CITY_LIST("City_List", "City List", "List of Cities"),
		CITY_LIST_PROFILE("city_list_profile", "City List (via Profile)", "List of Cities (via Profile)"),
		POST_LIST("POST_List", "POST List", "List of POST codes"),
		BROWSER_LIST("Browser_List", "Browser List", "List of Browsers"),
		OS_LIST("OS_List", "OS List", "List of OS's"),
		PLAYER_LIST("Player_List", "Player List", "List of Player's"),
		DUPLICATE_CHECK("Duplicate_Check", "Duplicate check", "Duplicate checks");

		@Getter
		@Accessors(fluent = true)
		private String type;

		@Getter
		@Accessors(fluent = true)
		private String displayName;

		@Getter
		@Accessors(fluent = true)
		private String description;
	}
}
