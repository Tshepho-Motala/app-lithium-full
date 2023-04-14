package lithium.service.kyc.provider.onfido.service;

import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Address;
import com.onfido.models.Applicant;
import com.onfido.models.Location;
import com.onfido.models.SdkToken;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.onfido.config.ProviderConfig;
import lithium.service.kyc.provider.onfido.entitites.UserApplicant;
import lithium.service.kyc.provider.onfido.exceptions.Status411FailOnfidoServiceException;
import lithium.service.kyc.provider.onfido.exceptions.Status412NotFoundApplicantException;
import lithium.service.kyc.provider.onfido.objects.ApplicantDto;
import lithium.service.kyc.provider.onfido.repositories.UserApplicantRepository;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
@AllArgsConstructor
public class OnfidoApplicantService extends OnfidoBaseService {
    private final UserApiInternalClientService userApiInternalClientService;
    private final UserApplicantRepository userApplicantRepository;

    public String getApplicantIdByGuid(String guid) throws Status412NotFoundApplicantException {
        return userApplicantRepository.findByUserGuid(guid)
                .map(UserApplicant::getApplicantId)
                .orElseThrow(() -> {
                    log.error("Can't find related UserApplicant, guid: " + guid);
                    return new Status412NotFoundApplicantException("Can't find related UserApplicant, guid: " + guid);
                });
    }

    public ApplicantDto getApplicantDto(LithiumTokenUtil tokenUtil) throws Status411FailOnfidoServiceException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        Onfido onfido = getOnfidoClient(tokenUtil.domainName());
        String guid = tokenUtil.guid();
        String applicantId = updateAndGetApplicantId(guid, tokenUtil.domainName(), onfido);
        return new ApplicantDto(applicantId, generateSdkToken(onfido, applicantId));
    }
    public String updateAndGetApplicantId(String guid, String domainName, Onfido onfido) throws Status411FailOnfidoServiceException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        Optional<UserApplicant> optionalUserApplicant = userApplicantRepository.findByUserGuid(guid);
        String applicantId;
        if (optionalUserApplicant.isPresent()) {
            UserApplicant userApplicant = optionalUserApplicant.get();
            applicantId = userApplicant.getApplicantId();
            updateApplicant(onfido, applicantId, guid);
            return applicantId;
        }

        Applicant applicant = createApplicant(onfido, guid);
        UserApplicant userApplicant = userApplicantRepository.save(
                UserApplicant.builder()
                        .applicantId(applicant.getId())
                        .domainName(domainName)
                        .userGuid(guid)
                        .build());
        log.info("User applicant saved: " + userApplicant);
        applicantId = userApplicant.getApplicantId();
        return applicantId;
    }

    private String generateSdkToken(Onfido onfido, String applicantId) throws Status411FailOnfidoServiceException {
        try {
            String token = onfido.sdkToken.generate(SdkToken.request().applicantId(applicantId));
            log.debug("Sdk token  generated (" + applicantId + "): " + token);
            return token;
        } catch (OnfidoException e) {
            log.error("Can't generate sdk token (" + applicantId + ") due " + e.getMessage(), e);
            throw new Status411FailOnfidoServiceException("Can't generate sdk token due " + e.getMessage());
        }
    }

    private void updateApplicant(Onfido onfido, String applicantId, String userGuid) throws Status500InternalServerErrorException, Status411FailOnfidoServiceException {
        try {
            Applicant applicant = onfido.applicant.find(applicantId);
            Applicant.Request applicantRequest = prepareApplicantRequest(userGuid);
            if (isApplicantActual(applicant, applicantRequest)) {
                log.info("Applicant (" + applicantId + ") is actual");
                return;
            }
            applicant = onfido.applicant.update(applicantId, applicantRequest);
            log.debug("Applicant updated (" + userGuid + "): " + applicant);
        } catch (OnfidoException e) {
            log.error("Can't update applicant (" + userGuid + ") due " + e.getMessage(), e);
            throw new Status411FailOnfidoServiceException("Can't update applicant due " + e.getMessage());
        }
    }

    private boolean isApplicantActual(Applicant applicant, Applicant.Request applicantRequest) {
        return Objects.equals(applicant.getFirstName(), applicantRequest.getFirstName())
                && Objects.equals(applicant.getLastName(), applicantRequest.getLastName())
                && Objects.equals(applicant.getDob(), applicantRequest.getDob())
                && isApplicantAddressActual(applicant.getAddress(), applicantRequest.getAddress())
                && isApplicantLocationActual(applicant.getLocation(), applicantRequest.getLocation());
    }

    private boolean isApplicantAddressActual(Address address, Address.Request requestAddress) {
        if (isNull(address) && isNull(requestAddress)) {
            return true;
        }
        if (isNull(address) || isNull(requestAddress)) {
            return false;
        }
        return Objects.equals(address.getCountry(), requestAddress.getCountry())
                && Objects.equals(address.getPostcode(), requestAddress.getPostcode())
                && Objects.equals(address.getTown(), requestAddress.getTown())
                && Objects.equals(address.getStreet(), requestAddress.getStreet());
    }

    private boolean isApplicantLocationActual(Location location, Location.Request requestLocation) {
        if (isNull(location) && isNull(requestLocation)) {
            return true;
        }
        if (isNull(location) || isNull(requestLocation)) {
            return false;
        }
        return Objects.equals(location.getCountryOfResidence(), requestLocation.getCountryOfResidence());
    }

    private Applicant createApplicant(Onfido onfido, String userGuid) throws Status500InternalServerErrorException, Status411FailOnfidoServiceException, Status512ProviderNotConfiguredException {
        try {
            Applicant.Request applicantRequest = prepareApplicantRequest(userGuid);
            Applicant applicant = onfido.applicant.create(applicantRequest);
            log.debug("Applicant created (" + userGuid + "): " + applicant);
            return applicant;
        } catch (OnfidoException e) {
            log.error("Can't create applicant (" + userGuid + ") due " + e.getMessage(), e);
            throw new Status411FailOnfidoServiceException("Can't create applicant due " + e.getMessage());
        }
    }

    private Applicant.Request prepareApplicantRequest(String userGuid) throws Status500InternalServerErrorException {
        try {
            User user = userApiInternalClientService.getUserByGuid(userGuid);
            Applicant.Request request = Applicant.request()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .dob(User.getDobAsLocalDate(user));
            lithium.service.user.client.objects.Address residentialAddress = user.getResidentialAddress();
            if (nonNull(residentialAddress)) {
                String countryCode = ofNullable(user.getCountryCode()).map(this::iso2CountryCodeToIso3CountryCode).orElse(null);
                String addressLine = residentialAddress.getAddressLine1();
                if (nonNull(addressLine) && addressLine.length() > 32) {
                    addressLine = addressLine.substring(0, 31);
                    log.warn("Street line length of " + userGuid + " is too long, trimmed: " + addressLine);
                }
                Address.Request address = Address.request()
                        .town(residentialAddress.getCity())
                        .street(addressLine)
                        .postcode(residentialAddress.getPostalCode())
                        .country(countryCode);
                request.address(address);
                request.location(Location.request().countryOfResidence(countryCode));
            }
            log.debug("Applicant request (" + userGuid + "): " + request);
            return request;
        } catch (Throwable e) {
            log.error("Can't prepare applicant (" + userGuid + ") update request due " + e.getMessage());
            throw new Status500InternalServerErrorException("Can't prepare applicant update request due internal error", e);
        }
    }

    public User getUserByApplicantId(String applicantId) throws Status500InternalServerErrorException, Status412NotFoundApplicantException {
        String userGuid = userApplicantRepository.findByApplicantId(applicantId)
                .map(UserApplicant::getUserGuid)
                .orElseThrow(() -> {
                    log.error("Can't find related UserApplicant, applicantId: " + applicantId);
                    return new Status412NotFoundApplicantException("Can't find related UserApplicant, applicantId: " + applicantId);
                });
        try {
            return userApiInternalClientService.getUserByGuid(userGuid);
        } catch (Throwable e) {
            log.error("Can't retrieve user (" + userGuid + ") due " + e.getMessage(), e);
            throw new Status500InternalServerErrorException("Can't retrieve user due " + e.getMessage());
        }
    }

    public String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
        Locale locale = new Locale("", iso2CountryCode);
        return locale.getISO3Country();
    }
}
