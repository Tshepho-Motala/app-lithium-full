package lithium.service.access.provider.kycgbg.controllers;

import com.id3global.id3gws._2013._04.AuthenticateSPElement;
import com.id3global.id3gws._2013._04.AuthenticateSPResponseElement;
import com.id3global.id3gws._2013._04.GlobalAddressType;
import com.id3global.id3gws._2013._04.GlobalAddressesType;
import com.id3global.id3gws._2013._04.GlobalCanadaSocialInsuranceNumberType;
import com.id3global.id3gws._2013._04.GlobalCanadaType;
import com.id3global.id3gws._2013._04.GlobalContactDetailsType;
import com.id3global.id3gws._2013._04.GlobalIdentityDocumentsType;
import com.id3global.id3gws._2013._04.GlobalInputDataType;
import com.id3global.id3gws._2013._04.GlobalLandTelephoneType;
import com.id3global.id3gws._2013._04.GlobalPersonalDetailsType;
import com.id3global.id3gws._2013._04.GlobalPersonalType;
import com.id3global.id3gws._2013._04.GlobalProfileIDVersionType;
import com.id3global.id3gws._2013._04.GlobalResultDataType;
import lithium.service.access.provider.kycgbg.adapter.KycAdapter;
import lithium.service.access.provider.kycgbg.config.APIAuthentication;
import lithium.service.access.provider.kycgbg.config.KycGbgClientConfig;
import lithium.service.access.provider.kycgbg.config.ObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBElement;
@RestController
@Slf4j
public class PilotApiController extends BaseController {
	@RequestMapping("/authenticateSP") // Test data for sandbox. This is the only set of data that works on the test environment
	public GlobalResultDataType authenticateSP(APIAuthentication apiAuth) {
		log.info("Reached start of authenticatSP method");
		String profileID = apiAuth.getBrandConfiguration().getProfileId();
		String username = apiAuth.getBrandConfiguration().getUsername();
		String password = apiAuth.getBrandConfiguration().getPassword();
		String url = apiAuth.getBrandConfiguration().getBaseUrl()+"/Soap11_Auth";
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(KycGbgClientConfig.class);
		
		ObjectFactory obj = new ObjectFactory();
		AuthenticateSPElement entry = new AuthenticateSPElement();
		// create profile object
		GlobalProfileIDVersionType profile = new GlobalProfileIDVersionType();
		JAXBElement<Long> profileVersion = obj.createGlobalProfileIDVersionTypeVersion((long) 0);
		profile.setID(profileID);
		profile.setVersion(profileVersion);

		JAXBElement<GlobalProfileIDVersionType> jaxProfile = obj.createGlobalProfileIDVersion(profile);

		entry.setProfileIDVersion(jaxProfile);
		log.info("entry global profile: " + entry.getProfileIDVersion().getValue().getID());

		// create input data object
		GlobalInputDataType input = new GlobalInputDataType();
		// Personal subsection
		GlobalPersonalType personal = new GlobalPersonalType();
		GlobalPersonalDetailsType personalDetails = new GlobalPersonalDetailsType();
		JAXBElement<String> surname = obj.createGlobalPersonalDetailsTypeSurname("Girlie");
		personalDetails.setSurname(surname);
		JAXBElement<String> middlename = obj.createGlobalPersonalDetailsTypeMiddleName("Ava");
		personalDetails.setMiddleName(middlename);
		JAXBElement<String> forename = obj.createGlobalPersonalDetailsTypeForename("Girl");
		personalDetails.setForename(forename);
		JAXBElement<Integer> dobday = obj.createGlobalPersonalDetailsTypeDOBDay(2);
		personalDetails.setDOBDay(dobday);
		JAXBElement<Integer> dobmonth = obj.createGlobalPersonalDetailsTypeDOBMonth(2);
		personalDetails.setDOBMonth(dobmonth);
		JAXBElement<Integer> dobyear = obj.createGlobalPersonalDetailsTypeDOBYear(1960);
		personalDetails.setDOBYear(dobyear);
		JAXBElement<GlobalPersonalDetailsType> jaxPersonalDetails = obj
				.createGlobalPersonalTypePersonalDetails(personalDetails);
		personal.setPersonalDetails(jaxPersonalDetails);

		JAXBElement<GlobalPersonalType> jaxPersonal = obj.createGlobalInputDataTypePersonal(personal);
		input.setPersonal(jaxPersonal);
		log.debug("entry personal object: "
				+ input.getPersonal().getValue().getPersonalDetails().getValue().getSurname().getValue());

		// Address subsection
		GlobalAddressesType address = obj.createGlobalAddressesType();
		GlobalAddressType addressDetails = obj.createGlobalAddressType();
		JAXBElement<String> postcode = obj.createGlobalAddressTypeZipPostcode("M4M2Z7");
		addressDetails.setZipPostcode(postcode);
		JAXBElement<String> buildingnum = obj.createGlobalAddressTypeBuilding("137");
		addressDetails.setBuilding(buildingnum);
		JAXBElement<String> street = obj.createGlobalAddressTypeStreet("Jones Ave");
		addressDetails.setStreet(street);
		JAXBElement<String> city = obj.createGlobalAddressTypeCity("Toronto");
		addressDetails.setCity(city);
		JAXBElement<String> statedistrict = obj.createGlobalAddressTypeStateDistrict("ON");
		addressDetails.setStateDistrict(statedistrict);
		JAXBElement<String> country = obj.createGlobalAddressTypeCountry("Canada");
		addressDetails.setCountry(country);

		JAXBElement<GlobalAddressType> jaxAddressDetails = obj.createGlobalAddressesTypeCurrentAddress(addressDetails);
		address.setCurrentAddress(jaxAddressDetails);

		JAXBElement<GlobalAddressesType> jaxAddress = obj.createGlobalInputDataTypeAddresses(address);
		input.setAddresses(jaxAddress);
		log.debug("Entry address object: "
				+ input.getAddresses().getValue().getCurrentAddress().getValue().getStateDistrict().getValue());

		// identity documents subsection
		GlobalIdentityDocumentsType iddocuments = obj.createGlobalIdentityDocumentsType();
		GlobalCanadaType iddocumentsdetails = obj.createGlobalCanadaType();
		GlobalCanadaSocialInsuranceNumberType insurancenum = obj.createGlobalCanadaSocialInsuranceNumberType();
		JAXBElement<String> ssn = obj.createGlobalCanadaSocialInsuranceNumberTypeNumber("275448488");
		insurancenum.setNumber(ssn);
		JAXBElement<GlobalCanadaSocialInsuranceNumberType> jaxInsuranceNum = obj
				.createGlobalCanadaTypeSocialInsuranceNumber(insurancenum);
		iddocumentsdetails.setSocialInsuranceNumber(jaxInsuranceNum);

		JAXBElement<GlobalCanadaType> jaxIdDocumentsDetails = obj
				.createGlobalIdentityDocumentsTypeCanada(iddocumentsdetails);
		iddocuments.setCanada(jaxIdDocumentsDetails);
		JAXBElement<GlobalIdentityDocumentsType> jaxIdDocuments = obj
				.createGlobalInputDataTypeIdentityDocuments(iddocuments);
		input.setIdentityDocuments(jaxIdDocuments);
		log.debug("entry iddocs object: " + input.getIdentityDocuments().getValue().getCanada().getValue()
				.getSocialInsuranceNumber().getValue().getNumber().getValue());

		// contact info subsection
		GlobalContactDetailsType contactdetails = obj.createGlobalContactDetailsType();
		GlobalLandTelephoneType contactdetailsinfo = obj.createGlobalLandTelephoneType();
		JAXBElement<String> telNo = obj.createGlobalLandTelephoneTypeNumber("4162031245");
		contactdetailsinfo.setNumber(telNo);
		JAXBElement<GlobalLandTelephoneType> jaxContactdetailsinfo = obj.createGlobalLandTelephone(contactdetailsinfo);
		contactdetails.setLandTelephone(jaxContactdetailsinfo);

		JAXBElement<GlobalContactDetailsType> jaxContactdetails = obj.createGlobalContactDetails(contactdetails);
		input.setContactDetails(jaxContactdetails);
		log.debug(input.getContactDetails().getValue().getLandTelephone().getValue().getNumber().getValue());

		JAXBElement<GlobalInputDataType> jaxInput = obj.createGlobalInputData(input);
		entry.setInputData(jaxInput);

		KycAdapter client = context.getBean(KycAdapter.class);

		AuthenticateSPResponseElement res = client
				.authenticateSP(url, entry, username, password);

		log.info("reached end with result: " + res.getAuthenticateSPResult().getValue().getBandText().getValue());
		return res.getAuthenticateSPResult().getValue();
	}
	
}