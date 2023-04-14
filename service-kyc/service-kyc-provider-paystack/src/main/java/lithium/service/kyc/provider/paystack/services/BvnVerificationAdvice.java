package lithium.service.kyc.provider.paystack.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status426PlayerUnderAgeException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveData;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BvnVerificationAdvice {

    @Autowired
    private ApiService apiService;

    @Autowired
    private ChangeLogService changeLogService;

    public VerificationStatus advice(String userGuid, BvnResolveResponse bvnResolveResponse) throws ParseException, Status424KycVerificationUnsuccessfulException {
        if (isValid(userGuid, bvnResolveResponse)) {
            return VerificationStatus.EXTERNALLY_VERIFIED;
        }
        log.warn("Bvn response does not match player (" + userGuid + ") info.");
        throw new Status424KycVerificationUnsuccessfulException("Bvn response does not match player info.");
    }

    /**
     *
     * @param dob format yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public boolean isOver18(String dob) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = df.parse(dob);
        LocalDate today = LocalDate.now();
        LocalDate localDateOfBirth = parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(localDateOfBirth, today).getYears() >= 18;
    }

    public VerificationStatus currentStatus(String userGuid) throws Status425IllegalUserStateException, ParseException, Status426PlayerUnderAgeException, Status512ProviderNotConfiguredException, Status512ProviderNotConfiguredException, UserClientServiceFactoryException {
        apiService.getBrandsConfigurationBrand(userGuid);
        User user = apiService.getUser(userGuid);
        if (user == null || user.getDateOfBirth() == null) {
            saveChangeLog(userGuid, null, "Bvn Verification failed, date of birth not set");
            throw new Status425IllegalUserStateException("Logged in player date of birth is not set");
        }
        DateTime date = user.getDateOfBirth();
        String dob = date.getYear() +"-"+ date.getMonthOfYear() + "-" +date.getDayOfMonth();
        if (!isOver18(dob)) {
            saveChangeLog(userGuid, null, "Bvn Verification failed, player under age " + dob);
            throw new Status426PlayerUnderAgeException("User under age");
        }
        if (user.getVerificationStatus() != null) {
            saveChangeLog(userGuid, null, "Bvn Verification attempt on already verified, " + user.getVerificationStatus());
            return getVerificationStatusById(user.getVerificationStatus());
        }
        return null;
    }

    public boolean isValid(String userGuid, BvnResolveResponse bvnResult) throws ParseException {
        User user = apiService.getUser(userGuid);
        if (user == null || bvnResult == null) {
            saveChangeLog(userGuid, null, "Bvn Check Failed, empty result returned");
            return false;
        }
        if (StringUtils.isNotBlank(bvnResult.getStatus()) && "true".equalsIgnoreCase(bvnResult.getStatus())) {
            boolean nameResult = namesMatch(user, bvnResult.getData());
            boolean dobResult = dobMatch(user, bvnResult.getData());
            if (nameResult && dobResult){
                saveChangeLog(userGuid, bvnResult, "");
                return true;
            }
        }
        saveChangeLog(userGuid, bvnResult, "");
        return false;
    }

    /**
     * Date format used to compare id yyyy-MM-dd
     * @param user
     * @param bvnData
     * @return
     */
    private boolean dobMatch(User user,BvnResolveData bvnData) throws ParseException {
        String userDob = user.getDobYear()+"-"+user.getDobMonth()+"-"+user.getDobDay();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date userDate = df.parse(userDob);
        Date bvnDate = df.parse(bvnData.getFormattedDob());
        if (userDate.compareTo(bvnDate) == 0) {
            return true;
        }
        return false;
    }

    private boolean namesMatch(User user,BvnResolveData bvnData){
        if (StringUtils.equalsIgnoreCase(user.getFirstName(),bvnData.getFirstName()) && StringUtils.equalsIgnoreCase(user.getLastName(),bvnData.getLastName())) {
            return true;
        }
        return false;
    }

    private void saveChangeLog(String userGuid, BvnResolveResponse bvnResolveResponse, String comment) {
        try {
            User user = apiService.getUser(userGuid);
            List<ChangeLogFieldChange> clfc = new ArrayList<>();
            String result;
            if (bvnResolveResponse != null) {
                result = apiService.bvnResponseString(bvnResolveResponse);
            } else {
                result = comment;
            }
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), userGuid, null, result, "",
                clfc, Category.ACCOUNT, SubCategory.KYC, 1, user.getDomain().getName());
        } catch (Exception ex) {
            log.error("Failed to submit change log for {}, {}: {}", userGuid, bvnResolveResponse,  String.join("\\n", ExceptionUtils.getRootCauseStackTrace(ex)));
        }
    }


    private VerificationStatus getVerificationStatusById(Long userVerificationStatusId) throws UserClientServiceFactoryException {
        Optional<VerificationStatus> verificationStatusOpt = Arrays.asList(VerificationStatus.values())
                .stream().filter(verificationStatus -> verificationStatus.getId()==userVerificationStatusId)
                .collect(Collectors.toList()).stream().findFirst();

        if (verificationStatusOpt.isPresent()) {
            return verificationStatusOpt.get();
        } else {
            throw new UserClientServiceFactoryException("Verification status not found id = ["+userVerificationStatusId+"]");
        }
    }
}
