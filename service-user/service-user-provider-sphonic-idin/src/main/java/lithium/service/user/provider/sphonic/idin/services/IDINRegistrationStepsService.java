package lithium.service.user.provider.sphonic.idin.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status463IncompleteUserRegistrationException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.SystemUserClient;
import lithium.service.user.client.objects.ValidatePreRegistrationResponse;
import lithium.service.user.client.objects.PostRegistrationSteps;
import lithium.service.user.client.objects.ValidatePreRegistration;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.Email;
import lithium.service.user.client.objects.CellphoneNumber;
import lithium.service.user.client.objects.ContactDetails;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINRequest;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINResponse;
import lithium.service.user.provider.sphonic.idin.storage.entities.User;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINRequestReposistory;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINResponseRepository;
import lithium.service.user.provider.sphonic.idin.storage.repositories.UserRepository;
import lithium.service.user.provider.sphonic.idin.util.IDINUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IDINRegistrationStepsService {

    private IDINResponseRepository idinResponseRepository;
    private ChangeLogService changeLogService;
    private IDINRequestReposistory idinRequestReposistory;
    private UserRepository userRepository;
    private MessageSource messageSource;
    private ExternalProviderIDINToKycService idinToKycService;
    private LithiumServiceClientFactory clientFactory;
    private IDINUtil idinUtil;

    private final String authorGuid = "System";
    private final String field = "idinResponse";
    private final int stage = 2;

    @Autowired
    public IDINRegistrationStepsService(ChangeLogService changeLogService, IDINResponseRepository idinResponseRepository, IDINRequestReposistory idinRequestReposistory,
                                        UserRepository userRepository, MessageSource messageSource, ExternalProviderIDINToKycService idinToKycService, LithiumServiceClientFactory clientFactory,
                                        IDINUtil idinUtil) {
        this.changeLogService = changeLogService;
        this.idinResponseRepository = idinResponseRepository;
        this.idinRequestReposistory = idinRequestReposistory;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.idinToKycService = idinToKycService;
        this.clientFactory = clientFactory;
        this.idinUtil = idinUtil;
    }

    public Response<ValidatePreRegistrationResponse> doPostRegistrationSteps(PostRegistrationSteps postRegistrationSteps) {
        return registrationValidations(postRegistrationSteps);
    }

    public void createIDINContactDetailsChangeLogs(lithium.service.user.client.objects.User user, String domainName, PostRegistrationSteps postRegSteps) {
        try {
            ArrayList<String> fieldStrings = new ArrayList<>();
            StringBuilder comments = new StringBuilder();
            IDINResponse idinResponse = IDINResponse.builder().build();
            String applicantHash = postRegSteps.getApplicantGuid().split("/")[1];

            if(!ObjectUtils.isEmpty(user.isEmailValidated()) && user.isEmailValidated()) {
                fieldStrings.add("emailValidated");
                comments.append(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_EMAIL_VALIDATED",
                        new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName}, LocaleContextHolder.getLocale()));
            }

            if(!ObjectUtils.isEmpty(user.isCellphoneValidated()) && user.isCellphoneValidated()) {
                fieldStrings.add("cellphoneValidated");
                if (comments.length() > 0) {
                    comments.append(", ");
                }
                comments.append(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_CELLPHONE_VALIDATED",
                        new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName}, LocaleContextHolder.getLocale()));
            }

            if(!ObjectUtils.isEmpty(user.getAddressVerified()) && user.getAddressVerified()) {
                fieldStrings.add("addressVerified");
                if (comments.length() > 0) {
                    comments.append(", ");
                }comments.append(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.IDIN_REG_MESSAGE",
                        new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName}, LocaleContextHolder.getLocale()));
            }
            List<ChangeLogFieldChange> clfc = changeLogService.copy(user, new lithium.service.user.client.objects.User(), fieldStrings.toArray(new String[0]));

            IDINRequest idinRequest = idinRequestReposistory.findIDINRequestByIdinApplicantHash(applicantHash);
            idinResponse = idinResponseRepository.findIDINResponseByIdinRequestIdAndStage(idinRequest.id, stage);

            ChangeLogFieldChange clfs = ChangeLogFieldChange.builder()
                    .field(field)
                    .toValue(idinResponse.toString())
                    .build();
            clfc.add(clfs);

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "create", user.getId(), authorGuid, null,
                    comments.toString(), null, clfc, Category.ACCOUNT, SubCategory.IDIN_VERIFICATION, 0, domainName);
        } catch (Exception e) {
            log.error("Failed to create iDin changelog | userGuid " + user.getGuid() , e);
        }
    }

    public Response<ValidatePreRegistrationResponse> preRegistrationValidation(ValidatePreRegistration validatePreRegistration) {
        User idinUser = userRepository.findByGuid(validatePreRegistration.getApplicantGuid());
        IDINRequest idinRequest = idinRequestReposistory.findIDINRequestByIdinApplicantHash(validatePreRegistration.getApplicantGuid().split("/")[1]);
        IDINResponse idinResponse = idinResponseRepository.findFirstByIdinRequestIdOrderByIdDesc(idinRequest.getId());
        ValidatePreRegistrationResponse validatePreRegResponse = ValidatePreRegistrationResponse.builder()
                .registrationAllowed(idinUser.getAddressVerified())
                .lastStageCompleted(idinResponse.getStage())
                .build();

        return Response.<ValidatePreRegistrationResponse>builder().data(validatePreRegResponse).build();
    }

    public boolean validateEmail(String preRegEmail, String userEmail) {
        if(!StringUtil.isEmpty(preRegEmail) && !StringUtil.isEmpty(userEmail)) {
            return preRegEmail.equalsIgnoreCase(userEmail);
        }
        return false;
    }

    public boolean validatePhone(String preRegCellPhone, String userCellPhone) {
        String formattedUserCellNumber = idinUtil.cellphoneNumberRemoveInternationalFormat(userCellPhone);
        String formattedPreRegCellNumber = idinUtil.cellphoneNumberRemoveInternationalFormat(preRegCellPhone);
        if(!StringUtil.isEmpty(formattedPreRegCellNumber) && !StringUtil.isEmpty(formattedUserCellNumber)) {
            return formattedUserCellNumber.equals(formattedPreRegCellNumber);
        }
        return false;
    }

    private Response<ValidatePreRegistrationResponse> registrationValidations(PostRegistrationSteps postRegistrationSteps) {
        Response<VerificationResult> verificationResultResponse;
        ValidatePreRegistration validatePreRegistration = ValidatePreRegistration.builder()
                .applicantGuid(postRegistrationSteps.getApplicantGuid())
                .build();
        Response<ValidatePreRegistrationResponse> response = preRegistrationValidation(validatePreRegistration);

        if(!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.getData()) && !response.getData().getRegistrationAllowed()) {
            String parameter = "iDinApplicantHash " + postRegistrationSteps.getApplicantGuid().split("/")[1];
            throw new Status463IncompleteUserRegistrationException(RegistrationError.INCOMPLETE_USER_REGISTRATION.getResponseMessageLocal(messageSource, postRegistrationSteps.getApplicantGuid().split("/")[0], new Object[]{parameter}));
        }

        lithium.service.user.client.objects.User user = validateContactDetails(postRegistrationSteps);
        if(!ObjectUtils.isEmpty(user)) {
            createIDINContactDetailsChangeLogs(user, user.getDomain().getName(), postRegistrationSteps);
        }
        log.info("User {} contact details were validated emailValidate:{} , cellphoneValidated: {}, addressVerify: {}", user.getGuid(),
                user.isEmailValidated(), user.isCellphoneValidated(), user.getAddressVerified());

        verificationResultResponse = idinToKycService.kycExternalProvider(postRegistrationSteps, user);
        ValidatePreRegistrationResponse validationRes = response.getData();
        validationRes.setKycSuccess(!ObjectUtils.isEmpty(verificationResultResponse) && verificationResultResponse.isSuccessful());
        validationRes.setCellphoneValidated(user.isCellphoneValidated());
        validationRes.setEmailValidated(user.isEmailValidated());
        validationRes.setUserGuid(user.guid());
        return Response.<ValidatePreRegistrationResponse>builder().data(validationRes).build();
    }

    private lithium.service.user.client.objects.User validateContactDetails(PostRegistrationSteps postRegistrationSteps) {
        lithium.service.user.client.objects.User user = null;
        try {
            User iDinUser = userRepository.findByGuid(postRegistrationSteps.getApplicantGuid());
            SystemUserClient userSystemAddressVerify = clientFactory.target(SystemUserClient.class, "service-user", true);
            String domainName = postRegistrationSteps.getApplicantGuid().split("/")[0];

            boolean userEmailValidated = validateEmail(postRegistrationSteps.getEmail(), iDinUser.getEmail());
            boolean cellphoneValidated = validatePhone(postRegistrationSteps.getCellphoneNumber(), iDinUser.getCellphoneNumber());
            iDinUser.setEmailValidated(userEmailValidated);
            iDinUser.setCellphoneValidated(cellphoneValidated);
            userRepository.save(iDinUser);

            Address address = Address.builder()
                    .addressVerified(true)
                    .category(Category.ACCOUNT.getName())
                    .subCategory(SubCategory.IDIN_VERIFICATION.getName())
                    .comment(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_ADDRESS_VERIFIED",
                            new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName},
                            LocaleContextHolder.getLocale()))
                    .build();
            CellphoneNumber cellphoneNumberValidate = CellphoneNumber.builder()
                    .cellphoneValidated(cellphoneValidated)
                    .comment(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_CELLPHONE_VALIDATED",
                            new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName},
                            LocaleContextHolder.getLocale()))
                    .build();
            Email email = Email.builder()
                    .emailValidated(userEmailValidated)
                    .comment(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_EMAIL_VALIDATED",
                            new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName},
                            LocaleContextHolder.getLocale()))
                    .build();

            ContactDetails contactDetailsValidate = ContactDetails.builder()
                    .userGuid(domainName + "/" + postRegistrationSteps.getUserId())
                    .address(address)
                    .cellphoneNumberValidate(cellphoneNumberValidate)
                    .emailValidate(email)
                    .contactVerifiedType(Category.ACCOUNT.getName())
                    .subCategory(SubCategory.IDIN_VERIFICATION.getName())
                    .build();
            user = userSystemAddressVerify.validateContactDetails(contactDetailsValidate);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Error occurred when trying to reach external clients :" + e);
        }
        return user;
    }
}
