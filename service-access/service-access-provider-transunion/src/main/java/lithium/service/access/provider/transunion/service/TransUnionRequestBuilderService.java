package lithium.service.access.provider.transunion.service;

import lithium.service.access.provider.transunion.config.TransUnionProviderConfig;
import lithium.service.access.provider.transunion.exeptions.UserIndividualsNotSetupException;
import lithium.service.user.client.objects.User;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static lithium.service.access.provider.transunion.shema.SearchRequestSOAPQNames.*;

@Service
public class TransUnionRequestBuilderService {

    public static final String WATCH_LISTS_DOB_STRICT = "true";
    public static final String WATCH_LISTS_SEARCH_TOLERANCE = "near";
    public static final String COUNTRY_CODE_GB = "GB";
    public static final String COUNTRY_CODE_GBR = "GBR";

    public SOAPMessage createVerifyRequest(User user, TransUnionProviderConfig config) throws SOAPException, UserIndividualsNotSetupException {
        String contentType = "application/soap+xml;charset=UTF-8;action=\"http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0/ISingleAccessPointService/Search\"";
        SOAPMessage soapMsg = createMessage(false);
        addHeader(soapMsg, config);
        addBody(soapMsg, user, config);
        soapMsg.saveChanges();
        soapMsg.getMimeHeaders().setHeader("Content-Type", contentType);
        return soapMsg;
    }

    private SOAPMessage createMessage(boolean adminNsUse) throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMsg = factory.createMessage();
        soapMsg.getSOAPPart().getEnvelope().removeNamespaceDeclaration("env");
        soapMsg.getSOAPPart().getEnvelope().addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        if (adminNsUse){
            soapMsg.getSOAPPart().getEnvelope().addNamespaceDeclaration("ns", "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0");
        } else {
            soapMsg.getSOAPPart().getEnvelope().addNamespaceDeclaration("ns", "http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointService/1.0");
        }
        soapMsg.getSOAPPart().getEnvelope().setPrefix("soap");
        return soapMsg;
    }


    public void addHeader(SOAPMessage soapMsg, TransUnionProviderConfig config) throws SOAPException {
        String username = config.getUserName();
        String password = config.getPassword();
        String company = config.getCompany();
        SOAPHeader soapHeader = soapMsg.getSOAPPart().getEnvelope().getHeader();
        soapHeader.removeContents();
        soapHeader.setPrefix("soap");
        soapHeader.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        SOAPHeaderElement headerElement = soapHeader.addHeaderElement(qNameHeaderSecurity);
        SOAPElement userNameToken = headerElement.addChildElement(qNameUserNameToken);
        userNameToken.removeContents();
        SOAPElement usernameValue = userNameToken.addChildElement(qNameUser);
        usernameValue.addTextNode(company + "\\" + username);
        SOAPElement passwordValue = userNameToken.addChildElement(qNamePassword);
        passwordValue.addTextNode(password);
    }

    public void addBody(SOAPMessage soapMsg, User user, TransUnionProviderConfig config) throws SOAPException, UserIndividualsNotSetupException {
        SOAPBody soapBody = soapMsg.getSOAPBody();
        soapBody.setPrefix("soap");
        soapBody.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        SOAPBodyElement search = soapBody.addBodyElement(qNameSearch);
        SOAPElement request = search.addChildElement(qNameRequest);
        addRequest(request, user, config);
    }

    public void addRequest(SOAPElement request, User user, TransUnionProviderConfig config) throws SOAPException, UserIndividualsNotSetupException {

        SOAPElement referenceID = request.addChildElement(qNameYourReferenceId);
        referenceID.addTextNode(UUID.randomUUID().toString());

        SOAPElement individuals = request.addChildElement(qNameIndividuals);
        addIndividuals(individuals, user);

        SOAPElement productToCall = request.addChildElement(qNameProductsToCall);
        addProductToCall(productToCall, config);
    }

    public void addIndividuals(SOAPElement individuals, User user) throws SOAPException, UserIndividualsNotSetupException {

        SOAPElement individual = individuals.addChildElement(qNameIndividual);
        SOAPElement dateOfBirth = individual.addChildElement(qNameDateOfBirth);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getDateOfBirth() == null)
            throw new UserIndividualsNotSetupException(491, "User date of birth is not set");
        String dob = formatter.format(user.getDateOfBirth().toDate());
        dateOfBirth.addTextNode(dob);
        SOAPElement names = individual.addChildElement(qNameNames);
        SOAPElement name = names.addChildElement(qNameName);

        if (user.getGender() == null) {
            name.addChildElement(qNameTitle).addTextNode("Unknown");
        } else {
            name.addChildElement(qNameTitle).addTextNode(user.getGender().equalsIgnoreCase("Male") ? "Mr" : "Ms");
        }

        if (user.getFirstName() == null || user.getLastName() == null)
            throw new UserIndividualsNotSetupException(494, "User name or surname is not set");

        name.addChildElement(qNameGivenName).addTextNode(user.getFirstName());
        name.addChildElement(qNameFamilyName1).addTextNode(user.getLastName());


        SOAPElement addresses = individual.addChildElement(qNameAddresses);
        addAddress(addresses.addChildElement(qNameAddress), user);

        SOAPElement contact = individual.addChildElement(qNameContact);
        addEmails(contact.addChildElement(qNameEmails), user);
        addTelephones(contact.addChildElement(qNameTelephones), user);

        SOAPElement applicationSettings = individual.addChildElement(qNameApplicationSettings);
        SOAPElement thirdPartyOptOut = applicationSettings.addChildElement(qNameThirdPartyOptOut);
        thirdPartyOptOut.addTextNode("false");
    }

    public void addTelephones(SOAPElement telephones, User user) throws SOAPException, UserIndividualsNotSetupException {
        SOAPElement telephone = telephones.addChildElement(qNameTelephone);
        QName telephoneTypeAttributeQName = new QName("TelephoneType");
        telephone.addAttribute(telephoneTypeAttributeQName, "15");
        if (user.getCellphoneNumber() == null)
            throw new UserIndividualsNotSetupException(492, "User cellphone is not set");
        if (user.getCellphoneNumber().substring(0,2).equalsIgnoreCase("44")){
            telephone.addChildElement(qNameNumber).addTextNode(user.getCellphoneNumber().substring(2));
        } else {
            telephone.addChildElement(qNameNumber).addTextNode(user.getCellphoneNumber());
        }
    }

    public void addEmails(SOAPElement emails, User user) throws SOAPException, UserIndividualsNotSetupException {
        SOAPElement email = emails.addChildElement(qNameEmail);
        QName emailAttributeQName = new QName("EmailType");
        if (user.getEmail() == null)
            throw new UserIndividualsNotSetupException(493, "User email is not set");
        email.addChildElement(qNameEmailAddress).addTextNode(user.getEmail());
        email.addAttribute(emailAttributeQName, "03");
    }

    public void addAddress(SOAPElement address, User user) throws SOAPException, UserIndividualsNotSetupException {
        if (user.getResidentialAddress().getAddressLine1() == null || user.getResidentialAddress().getCity() == null
                || user.getResidentialAddress().getPostalCode() == null)
            throw new UserIndividualsNotSetupException(495, "User address is not set, mandatory field: AddressLine1, city, postalCode");
        address.addChildElement(qNameLine3).addTextNode(user.getResidentialAddress().getAddressLine1());

        if (user.getResidentialAddress().getAddressLine2() != null && !user.getResidentialAddress().getAddressLine2().equalsIgnoreCase("")) {
            address.addChildElement(qNameLine2).addTextNode(user.getResidentialAddress().getAddressLine2());
        }
        if (user.getResidentialAddress().getAddressLine3() != null && !user.getResidentialAddress().getAddressLine3().equalsIgnoreCase("")) {
            address.addChildElement(qNameLine4).addTextNode(user.getResidentialAddress().getAddressLine3());
        }
        address.addChildElement(qNameLine8).addTextNode(user.getResidentialAddress().getCity());
        address.addChildElement(qNameLine10).addTextNode(user.getResidentialAddress().getPostalCode());
        if (user.getResidentialAddress().getCountryCode() != null) {
            String countryCode = user.getResidentialAddress().getCountryCode();
            if (countryCode.equalsIgnoreCase(COUNTRY_CODE_GB)) {
                countryCode = COUNTRY_CODE_GBR;
            }
            address.addChildElement(qNameCountryCode).addTextNode(countryCode);
        } else {
            throw new UserIndividualsNotSetupException(496, "User address is not set, mandatory field: Country Code");
        }
    }

    public void addProductToCall(SOAPElement qNameProductsToCall, TransUnionProviderConfig config) throws SOAPException {
        String application = config.getApplication();

        SOAPElement callValidate5 = qNameProductsToCall.addChildElement(qNameCallValidate5);

        callValidate5.addChildElement(qNameApplication).addTextNode(application);
        SOAPElement watchlistConfiguration = callValidate5.addChildElement(qNameCheckConfiguration).addChildElement(qNameWatchlistConfiguration);
        watchlistConfiguration.addChildElement(qNameWatchlistSearchTolerance).addTextNode(WATCH_LISTS_SEARCH_TOLERANCE);
        watchlistConfiguration.addChildElement(qNameWatchlistDateOfBirthStrict).addTextNode(WATCH_LISTS_DOB_STRICT);
    }

    public SOAPMessage createPasswordChangeRequest(TransUnionProviderConfig config, String newPassword) throws SOAPException {
        String contentType = "application/soap+xml;charset=UTF-8;action=\"http://www.callcredit.co.uk/SingleAccessPointService/ISingleAccessPointAdminService/1.0/ISingleAccessPointAdminService/ExecuteChangePassword\"";

        SOAPMessage soapMsg = createMessage(true);
        addHeader(soapMsg, config);
        addChangePasswordBody(soapMsg, newPassword, config);
        soapMsg.saveChanges();
        soapMsg.getMimeHeaders().setHeader("Content-Type", contentType);
        return soapMsg;
    }

    private void addChangePasswordBody(SOAPMessage soapMsg, String newPassword, TransUnionProviderConfig config) throws SOAPException {
            SOAPBody soapBody = soapMsg.getSOAPBody();
            soapBody.setPrefix("soap");
            soapBody.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
            SOAPBodyElement search = soapBody.addBodyElement(qNameExecuteChangePassword);
            SOAPElement request = search.addChildElement(qNameRequestForChangePassword);
            addChangePasswordRequest(request, newPassword, config);
    }

    private void addChangePasswordRequest(SOAPElement request, String newPassword, TransUnionProviderConfig config) throws SOAPException {
        SOAPElement aliasAndUsername = request.addChildElement(qNameAliasAndUsername);
        aliasAndUsername.addTextNode(config.getCompany() + "\\" + config.getUserName());

        SOAPElement currentPassword = request.addChildElement(qNameCurrentPassword);
        currentPassword.addTextNode(config.getPassword());

        SOAPElement newPasswordSOAP = request.addChildElement(qNameNewPassword);
        newPasswordSOAP.addTextNode(newPassword);

        SOAPElement referenceID = request.addChildElement(qNameYourReferenceId);
        referenceID.addTextNode(UUID.randomUUID().toString());
    }
}
