package lithium.service.document.client.objects.mail;

import lithium.service.client.objects.placeholders.Placeholder;

import java.util.HashSet;
import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_ADDRESS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_LINK_1;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_LINK_2;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_NAME_1;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_NAME_2;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_TIMESTAMP_1;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_FILE_TIMESTAMP_2;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOCUMENT_TYPE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.DOMAIN_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_ACCOUNT_STATUS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_ADDRESS_VERIFIED;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_AGE_VERIFIED;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_GUID;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_PLAYER_LINK;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_RESIDENTIAL_ADDRESS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_VERIFICATION_STATUS;


public class DwhNotificationPlaceholdersBuilder {
    private String domainName;
    private String playerGuid;
    private String playerLink;
    private String accountStatus;
    private String verificationStatus;
    private String ageVerified;
    private String addressVerified;
    private String fileName1;
    private String fileLink1;
    private String fileTimestamp1;
    private String fileName2;
    private String fileLink2;
    private String fileTimestamp2;
    private String documentType;
    private String documentAddress;
    private String residentialAddress;

    public DwhNotificationPlaceholdersBuilder setDomainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setPlayerGuid(String playerGuid) {
        this.playerGuid = playerGuid;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setPlayerLink(String playerLink) {
        this.playerLink = playerLink;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setAgeVerified(String ageVerified) {
        this.ageVerified = ageVerified;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setAddressVerified(String addressVerified) {
        this.addressVerified = addressVerified;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileName1(String fileName1) {
        this.fileName1 = fileName1;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileLink1(String fileLink1) {
        this.fileLink1 = fileLink1;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileName2(String fileName2) {
        this.fileName2 = fileName2;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileLink2(String fileLink2) {
        this.fileLink2 = fileLink2;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileTimestamp1(String fileTimestamp1) {
        this.fileTimestamp1 = fileTimestamp1;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setFileTimestamp2(String fileTimestamp2) {
        this.fileTimestamp2 = fileTimestamp2;
        return this;
    }

    public DwhNotificationPlaceholdersBuilder setDocumentType(String documentType) {
        this.documentType = documentType;
        return this;
    }
    public DwhNotificationPlaceholdersBuilder setDocumentAddress(String documentAddress) {
        this.documentAddress = documentAddress;
        return this;
    }
    public DwhNotificationPlaceholdersBuilder setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
        return this;
    }

    public Set<Placeholder> build() {
        Set<Placeholder> placeholders = new HashSet<>();
        placeholders.add(DOMAIN_NAME.from(domainName));
        placeholders.add(USER_GUID.from(playerGuid));
        placeholders.add(USER_PLAYER_LINK.from(playerLink));
        placeholders.add(USER_ACCOUNT_STATUS.from(accountStatus));
        placeholders.add(USER_VERIFICATION_STATUS.from(verificationStatus));
        placeholders.add(USER_AGE_VERIFIED.from(ageVerified));
        placeholders.add(USER_ADDRESS_VERIFIED.from(addressVerified));
        placeholders.add(DOCUMENT_FILE_NAME_1.from(fileName1));
        placeholders.add(DOCUMENT_FILE_LINK_1.from(fileLink1));
        placeholders.add(DOCUMENT_FILE_TIMESTAMP_1.from(fileTimestamp1));
        placeholders.add(DOCUMENT_FILE_NAME_2.from(fileName2));
        placeholders.add(DOCUMENT_FILE_LINK_2.from(fileLink2));
        placeholders.add(DOCUMENT_FILE_TIMESTAMP_2.from(fileTimestamp2));
        placeholders.add(DOCUMENT_TYPE.from(documentType));
        placeholders.add(DOCUMENT_ADDRESS.from(documentAddress));
        placeholders.add(USER_RESIDENTIAL_ADDRESS.from(residentialAddress));
        return placeholders;
    }
}
