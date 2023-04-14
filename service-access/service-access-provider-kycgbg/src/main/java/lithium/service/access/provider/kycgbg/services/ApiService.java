package lithium.service.access.provider.kycgbg.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.id3global.id3gws._2013._04.AuthenticateSPElement;
import com.id3global.id3gws._2013._04.AuthenticateSPResponseElement;
import com.id3global.id3gws._2013._04.CheckCredentialsElement;
import com.id3global.id3gws._2013._04.CheckCredentialsResponseElement;
import com.id3global.id3gws._2013._04.GlobalAddressType;
import com.id3global.id3gws._2013._04.GlobalAddressesType;
import com.id3global.id3gws._2013._04.GlobalContactDetailsType;
import com.id3global.id3gws._2013._04.GlobalInputDataType;
import com.id3global.id3gws._2013._04.GlobalItemCheckResultCodesType;
import com.id3global.id3gws._2013._04.GlobalLandTelephoneType;
import com.id3global.id3gws._2013._04.GlobalMobileTelephoneType;
import com.id3global.id3gws._2013._04.GlobalPersonalDetailsType;
import com.id3global.id3gws._2013._04.GlobalPersonalType;
import com.id3global.id3gws._2013._04.GlobalProfileIDVersionType;
import lithium.service.Response;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.kycgbg.KycgbgModuleInfo;
import lithium.service.access.provider.kycgbg.KycgbgModuleInfo.ConfigProperties;
import lithium.service.access.provider.kycgbg.adapter.KycAdapter;
import lithium.service.access.provider.kycgbg.config.BrandsConfigurationBrand;
import lithium.service.access.provider.kycgbg.config.GbgResponseData;
import lithium.service.access.provider.kycgbg.config.ObjectFactory;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ApiService {
	public static final String MATCH = "Match";
	@Autowired
	protected LithiumServiceClientFactory services;

	@Autowired
	KycgbgModuleInfo moduleInfo;
	
	@Bean
	public HashMap<String, String> stateMap() {
		HashMap<String, String> mapStates = new HashMap<String, String>();
		mapStates.put("ALBERTA", "AB");
		mapStates.put("BRITISH COLUMBIA", "BC");
		mapStates.put("MANITOBA", "MB");
		mapStates.put("NEW BRUNSWICK", "NB");
		mapStates.put("NEWFOUNDLAND AND LABRADOR", "NL");
		mapStates.put("NORTHWEST TERRITORIES", "NT");
		mapStates.put("NOVA SCOTIA", "NS");
		mapStates.put("NUNAVUT", "NU");
		mapStates.put("ONTARIO", "ON");
		mapStates.put("PRINCE EDWARD ISLAND", "PE");
		mapStates.put("QUEBEC", "QC");
		mapStates.put("SASKATCHEWAN", "SK");
		mapStates.put("YUKON", "YT");
		
		return mapStates;
	}
	
	public CheckCredentialsResponseElement checkCredentials(
		final KycAdapter adapter,
		final String username,
		final String password,
		final String url,
		RawAuthorizationData rawAuthorizationData
	) throws SocketTimeoutException {
		log.debug(url);
		ObjectFactory objFactory = new ObjectFactory();
		JAXBElement<String> accountname = objFactory.createCheckCredentialsElementAccountName(username);
		JAXBElement<String> accountpassword = objFactory.createCheckCredentialsElementPassword(password);
		log.debug("Accountname: " + accountname.getValue());
		log.debug("Password: " + accountpassword.getValue());
		
		CheckCredentialsElement input = objFactory.createCheckCredentialsElement();
		input.setAccountName(accountname);
		input.setPassword(accountpassword);

		log.debug("CheckCredentialsElement : "+input);
		populateRawData(rawAuthorizationData, input, null);
		CheckCredentialsResponseElement checkCredentialsResponseElement = adapter.checkCredentials(url, input);
		log.debug("CheckCredentialsResponseElement : "+checkCredentialsResponseElement);
		populateRawData(rawAuthorizationData, null, checkCredentialsResponseElement);
		return checkCredentialsResponseElement;
	}
	
	public GbgResponseData authenticateSP(
		KycAdapter adapter,
		User user,
		String profileID,
		String username,
		String password,
		String url,
		RawAuthorizationData rawAuthorizationData
	) {
		ObjectFactory obj = new ObjectFactory();
		AuthenticateSPElement entry = new AuthenticateSPElement();
		//create profile object- THIS WILL BE IN PROVIDER PROPERTIES
		GlobalProfileIDVersionType profile = new GlobalProfileIDVersionType();
		JAXBElement<Long> profileVersion = obj.createGlobalProfileIDVersionTypeVersion((long) 0);
		profile.setID(profileID);
		profile.setVersion(profileVersion);
		
		JAXBElement<GlobalProfileIDVersionType> jaxProfile = obj.createGlobalProfileIDVersion(profile);
		
		entry.setProfileIDVersion(jaxProfile);
		log.debug("Entry global profile: "+entry.getProfileIDVersion().getValue().getID());
		log.debug("Entry global profile version: "+entry.getProfileIDVersion().getValue().getVersion().getValue());

		//create customer reference (user guid)
		JAXBElement<String> customerReference = obj.createAuthenticateSPElementCustomerReference(user.getDomain().getName()+"/"+user.getUsername());
		entry.setCustomerReference(customerReference);

		//create input data object
		GlobalInputDataType input= new GlobalInputDataType();
		//Personal subsection
		GlobalPersonalType personal = new GlobalPersonalType();
		GlobalPersonalDetailsType personalDetails =new GlobalPersonalDetailsType();
		JAXBElement<String> surname = obj.createGlobalPersonalDetailsTypeSurname(user.getLastName());
		personalDetails.setSurname(surname);
		JAXBElement<String> forename = obj.createGlobalPersonalDetailsTypeForename(user.getFirstName());
		personalDetails.setForename(forename);
		JAXBElement<Integer> dobday = obj.createGlobalPersonalDetailsTypeDOBDay(user.getDobDay());
		personalDetails.setDOBDay(dobday);
		JAXBElement<Integer> dobmonth = obj.createGlobalPersonalDetailsTypeDOBMonth(user.getDobMonth());
		personalDetails.setDOBMonth(dobmonth);
		JAXBElement<Integer> dobyear = obj.createGlobalPersonalDetailsTypeDOBYear(user.getDobYear());
		personalDetails.setDOBYear(dobyear);
		JAXBElement<GlobalPersonalDetailsType> jaxPersonalDetails = obj.createGlobalPersonalTypePersonalDetails(personalDetails);
		personal.setPersonalDetails(jaxPersonalDetails);
		
		JAXBElement<GlobalPersonalType> jaxPersonal= obj.createGlobalInputDataTypePersonal(personal);
		input.setPersonal(jaxPersonal);
		log.debug("Entry personal object: " + input.getPersonal().getValue().getPersonalDetails().getValue().getSurname().getValue());

		//Address subsection
		GlobalAddressesType address = obj.createGlobalAddressesType();
		GlobalAddressType addressDetails= obj.createGlobalAddressType();
		String addressBuildingandStreet = user.getResidentialAddress().getAddressLine1();
		String[] addressBuildingandStreetArr = addressBuildingandStreet.split(" ",2);
		
		JAXBElement<String> country = obj.createGlobalAddressTypeCountry(user.getResidentialAddress().getCountry());
		addressDetails.setCountry(country);
		JAXBElement<String> buildingnum = obj.createGlobalAddressTypeBuilding(addressBuildingandStreetArr[0]);// stopped here. Read whole block
		addressDetails.setBuilding(buildingnum);
		JAXBElement<String> street = obj.createGlobalAddressTypeStreet(addressBuildingandStreetArr[1]);
		addressDetails.setStreet(street);
		JAXBElement<String> city = obj.createGlobalAddressTypeCity(user.getResidentialAddress().getCity());
		addressDetails.setCity(city);
		JAXBElement<String> postcode = obj.createGlobalAddressTypeZipPostcode(user.getResidentialAddress().getPostalCode());
		addressDetails.setZipPostcode(postcode);
		if (user.getResidentialAddress().getAdminLevel1() != null) {
			String province = user.getResidentialAddress().getAdminLevel1().toUpperCase();
			String mapProvince = stateMap().get(province);
			JAXBElement<String> statedistrict = obj.createGlobalAddressTypeStateDistrict(mapProvince);// this will be province field
			addressDetails.setStateDistrict(statedistrict);
			log.debug("Entry address object province: " + addressDetails.getStateDistrict().getValue());
		}

		JAXBElement<GlobalAddressType> jaxAddressDetails= obj.createGlobalAddressesTypeCurrentAddress(addressDetails);
		address.setCurrentAddress(jaxAddressDetails);

		JAXBElement<GlobalAddressesType> jaxAddress = obj.createGlobalInputDataTypeAddresses(address);
		input.setAddresses(jaxAddress);
		log.debug("Entry address object building num: "+input.getAddresses().getValue().getCurrentAddress().getValue().getBuilding().getValue() );
		log.debug("Entry address object street: "+input.getAddresses().getValue().getCurrentAddress().getValue().getStreet().getValue() );
		log.debug("Entry address object city: "+input.getAddresses().getValue().getCurrentAddress().getValue().getCity().getValue() );
		log.debug("Entry address object postcode: "+input.getAddresses().getValue().getCurrentAddress().getValue().getZipPostcode().getValue() );
		log.debug("Entry address object country: "+input.getAddresses().getValue().getCurrentAddress().getValue().getCountry().getValue() );

		//contact info subsection
		if (user.getTelephoneNumber() != null || user.getCellphoneNumber() != null) {
			GlobalContactDetailsType contactdetails = obj.createGlobalContactDetailsType();

			//add Telephone Number
			if (user.getTelephoneNumber() != null) {
				GlobalLandTelephoneType landTelephoneType = obj.createGlobalLandTelephoneType();
				JAXBElement<String> telNo = obj.createGlobalLandTelephoneTypeNumber(user.getTelephoneNumber());
				landTelephoneType.setNumber(telNo);
				JAXBElement<GlobalLandTelephoneType> jaxContactdetailsinfo = obj.createGlobalLandTelephone(landTelephoneType);
				contactdetails.setLandTelephone(jaxContactdetailsinfo);
				log.debug("Entry ContactDetails object landTelephone: " + contactdetails.getLandTelephone().getValue().getNumber().getValue());
			}

			//add Cellphone Number
			if (user.getCellphoneNumber() != null) {
				GlobalMobileTelephoneType mobileTelephoneType = obj.createGlobalMobileTelephoneType();
				JAXBElement<String> cellNo = obj.createGlobalMobileTelephoneTypeNumber(user.getCellphoneNumber());
				mobileTelephoneType.setNumber(cellNo);
				JAXBElement<GlobalMobileTelephoneType> globalMobileTelephoneTypeInfo = obj.createGlobalMobileTelephone(mobileTelephoneType);
				contactdetails.setMobileTelephone(globalMobileTelephoneTypeInfo);
				log.debug("Entry ContactDetails object mobileTelephone: " + contactdetails.getMobileTelephone().getValue().getNumber().getValue());
			}

			JAXBElement<GlobalContactDetailsType> jaxContactdetails = obj.createGlobalContactDetails(contactdetails);
			input.setContactDetails(jaxContactdetails);
		}

		JAXBElement<GlobalInputDataType> jaxInput = obj.createGlobalInputData(input);
		entry.setInputData(jaxInput);

		populateRawData(rawAuthorizationData, entry, null);
		AuthenticateSPResponseElement res = adapter.authenticateSP(url, entry, username, password); //Change this to live url
		populateRawData(rawAuthorizationData, null, res);
		GbgResponseData result = GbgResponseData.builder()
				.providerRequestId(res.getAuthenticateSPResult().getValue().getAuthenticationID())
				.bandText(res.getAuthenticateSPResult().getValue().getBandText().getValue())
				.scorePoints(res.getAuthenticateSPResult().getValue().getScore().getValue())
				.build();

		result = updateWithMatchedFields(res.getAuthenticateSPResult().getValue().getResultCodes().getValue().getGlobalItemCheckResultCodes(), result);
		log.info("Identity check for user: :" + user.getGuid() + " reached end with result: " + result);
		try {
			log.debug("Reached end with raw result: " + new ObjectMapper().writeValueAsString(res));
		} catch (JsonProcessingException e) {
			log.error("Unable to map response object to string in AuthenticateSPResponseElement");
		}

		return result;
	}


	private GbgResponseData updateWithMatchedFields(List<GlobalItemCheckResultCodesType> globalItemCheckResultCodes, GbgResponseData result) {
		for (GlobalItemCheckResultCodesType item : globalItemCheckResultCodes) {
			if (MATCH.equalsIgnoreCase(item.getDOB().value()))
				result.setDobMatched(true);
			if (MATCH.equalsIgnoreCase(item.getAddress().value()))
				result.setAddressMatched(true);
			if (MATCH.equalsIgnoreCase(item.getSurname().value()))
				result.setLastNameMatched(true);
		}
		return result;
	}
	
	public BrandsConfigurationBrand getBrandConfiguration(String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(moduleInfo.getModuleName(), domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for (ProviderProperty p: pp.getData()) {
			if (p.getName().equalsIgnoreCase(ConfigProperties.PROFILE_ID.getValue())) brandConfiguration.setProfileId(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.PEP_SANCTIONS_ID.getValue())) brandConfiguration.setPepSancID(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.USERNAME.getValue())) brandConfiguration.setUsername(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.PASSWORD.getValue())) brandConfiguration.setPassword(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.TIMEOUT_READ.getValue())) brandConfiguration.setReadTimeout((p.getValue()!=null)?Integer.parseInt(p.getValue()):null);
			if (p.getName().equalsIgnoreCase(ConfigProperties.TIMEOUT_CONNECTION.getValue())) brandConfiguration.setConnectionTimeout((p.getValue()!=null)?Integer.parseInt(p.getValue()):null);
		}
		return brandConfiguration;
	}
	
	public User getUser(String playerguid) {
		UserApiInternalClient cl = getUserService();
		Response<User> response = cl.getUser(playerguid);
		if (response.isSuccessful()) return response.getData();
		return null;
	}
	
	private UserApiInternalClient getUserService() {
		UserApiInternalClient cl = null;
		try {
			cl = services.target(UserApiInternalClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}
	
	
	private ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}

	/**
	 * Utility function to populate raw transaction data into a pre-initilized data object.
	 * The requestData and responseData parameters can be null and will then be ignored for the serialization operation.
	 * @param rawAuthorizationData (output)
	 * @param requestData (input)
	 * @param responseData (input)
	 */
	private void populateRawData(RawAuthorizationData rawAuthorizationData, final Object requestData, final Object responseData) {
		if (rawAuthorizationData == null) {
			log.error("Unable to produce raw transaction data since rawAuthorizationData object is not initioalized");
		}
		if (requestData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
			} catch (JsonProcessingException e) {
				log.warn("Unable to map raw transaction request for auth request: " + requestData, e);
			}
		}
		if (responseData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
				if (responseData != null) {
					rawAuthorizationData.setRawResponseFromProvider(new ObjectMapper().writeValueAsString(responseData));
				}
			} catch(JsonProcessingException e){
				log.warn("Unable to map raw transaction response for auth request: " + responseData, e);
			}
		}
	}
}
