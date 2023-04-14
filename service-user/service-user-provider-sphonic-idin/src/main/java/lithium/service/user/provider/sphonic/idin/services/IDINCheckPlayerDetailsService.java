package lithium.service.user.provider.sphonic.idin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.user.provider.sphonic.idin.objects.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * {@code IDINCheckPlayerDetailsService}
 * This service is used for checking and mapping iDin response data. Since the Sphonic responses are not stable and consistent
 * there is need to provide a safety net when some fields and values don't get returned by iDin API
 */
public class IDINCheckPlayerDetailsService {
    private final String NONE = "None";
    public AddressData checkAddressData(JSONObject jsonData) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String addressData = jsonData.getJSONObject("addressData").toString();
            final AddressData playerAddress = objectMapper.readValue(addressData, AddressData.class);
            boolean isHouseNumber = isPresent(playerAddress.getHouseNumber());
            boolean isStreet = isPresent(playerAddress.getStreet());
            boolean isCity = isPresent(playerAddress.getCity());
            boolean isPostalCode = isPresent(playerAddress.getPostalCode());
            boolean isCountryCode = isPresent(playerAddress.getCountryCode());

            if(isHouseNumber && isStreet && isCity && isPostalCode && isCountryCode) {
                return playerAddress;
            }
            log.debug("iDin provided Address could not be verified::" + addressData);
        } catch (Exception ex) {
            log.error("iDin failed to obtain address data : " + ex);
        }
        return null;
    }

    public AgeData checkAgeData(JSONObject jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String dobData = jsonData.getJSONObject("ageData").toString();
            AgeData data = objectMapper.readValue(dobData, AgeData.class);
            return data;
        } catch (Exception ex) {
            log.debug("iDin failed to obtain age data : ", ex);
        }
        return null;
    }

    public ContactData checkContactData(JSONObject jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String contactData = jsonData.getJSONObject("contactData").toString();
            ContactData data = objectMapper.readValue(contactData, ContactData.class);
            return data;
        } catch (Exception ex) {
            log.debug("iDin failed to obtain contact data : ", ex);
        }
        return null;
    }

    public NameData checkNameData(JSONObject jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String nameJsonData = jsonData.getJSONObject("nameData").toString();
            NameData data = objectMapper.readValue(nameJsonData, NameData.class);
            return data;
        } catch (Exception ex) {
            log.debug("iDin failed to obtain name data : ", ex);
        }
        return null;
    }

    public GenderData checkGenderData(JSONObject jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String genderData = jsonData.getJSONObject("genderData").toString();
            GenderData data = objectMapper.readValue(genderData, GenderData.class);
            return data;
        } catch (Exception ex) {
            log.debug("iDin failed to obtain gender data : ", ex);
        }
        return null;
    }

    public TraceData checkTraceData(JSONObject jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String traceData = jsonData.getJSONObject("traceData").toString();
            TraceData tData = objectMapper.readValue(traceData, TraceData.class);
            return tData;
        } catch (Exception ex) {
            log.debug("iDin failed to obtain trace data :  {} , exception : {}", jsonData.toString(), ex);
        }
        return null;
    }

    public boolean isPresent(String data) {
        return data != null && !data.isEmpty() && !data.equalsIgnoreCase(NONE);
    }
}
