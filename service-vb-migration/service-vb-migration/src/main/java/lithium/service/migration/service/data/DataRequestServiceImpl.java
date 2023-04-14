package lithium.service.migration.service.data;

import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lithium.service.libraryvbmigration.data.dto.MigrationCredential;
import lithium.service.libraryvbmigration.data.dto.MigrationPlayerBasic;
import lithium.service.migration.config.ServiceVbMigrationConfigProperties;
import lithium.service.migration.util.Util;
import lithium.service.migration.util.columns.Columns;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataRequestServiceImpl implements DataRequestService {

  private final ServiceVbMigrationConfigProperties properties;
  private final ModelMapper mapper;

  @Transactional
  public MigrationPlayerBasic getPlayerBasic(FieldValueList fieldValues, FieldList fields) {
    BigDecimal bdDateValue = new BigDecimal(Util.getStringFieldValue(fieldValues, Columns.REG_DATE.columnName, fields));
    String status = Util.getStringFieldValue(fieldValues, Columns.STATUS_NAME.columnName, fields);

    String email = Util.getStringFieldValue(fieldValues, Columns.EMAIL.columnName, fields);
    //EMAIL & SELF EXCLUSION/TERMINATED
    if (!ObjectUtils.isEmpty(email) && email.matches("(_trm_|_se_).*")) {
        email = Util.removeSelfExclusion(email);
    }
    PlayerBasic playerBasic = PlayerBasic.builder()
        .password(Util.getStringFieldValue(fieldValues, Columns.HASHED_PASSWORD.columnName, fields))
        .lastNamePrefix(Util.removeLastChar(fieldValues, Columns.TITLE.columnName, fields))
        .firstName(Util.getStringFieldValue(fieldValues, Columns.FIRST_NAME.columnName, fields))
        .lastName(Util.getStringFieldValue(fieldValues, Columns.LAST_NAME.columnName, fields))
        .username(Util.getStringFieldValue(fieldValues, Columns.USERNAME.columnName, fields))
        .email(email)
        .domainName(properties.getHistoricIngestion().getDomain())
        .gender(Util.getStringFieldValue(fieldValues, Columns.GENDER_1.columnName, fields))
        .countryCode(Util.getStringFieldValue(fieldValues, Columns.CODE.columnName, fields))
        .cellphoneNumber(Util.getStringFieldValue(fieldValues, Columns.MOBILE.columnName, fields))
        .residentialAddress(AddressBasic.builder()
            .addressLine1(Util.getStringFieldValue(fieldValues, Columns.ADDRESS_LINE.columnName, fields))
            .country(Util.getStringFieldValue(fieldValues, Columns.COUNTRY_NAME.columnName, fields))
            .postalCode(Util.getStringFieldValue(fieldValues, Columns.ZIP_CODE.columnName, fields))
            .city(Util.getStringFieldValue(fieldValues, Columns.CITY_1.columnName, fields))
            .countryCode(Util.getStringFieldValue(fieldValues, Columns.CODE.columnName, fields))
            .build())
        .affiliateGuid(Util.getStringFieldValue(fieldValues, Columns.B_TAG.columnName, fields))
        .emailOptOut(Util.getBooleanFieldValue(fieldValues, Columns.EMAIL_OPT_OUT.columnName, fields))
        .postOptOut(Util.getBooleanFieldValue(fieldValues, Columns.POST_OPT_OUT.columnName, fields))
        .callOptOut(Util.getBooleanFieldValue(fieldValues, Columns.CALL_OPT_OUT.columnName, fields))
        .pushOptOut(Util.getBooleanFieldValue(fieldValues, Columns.OPT_IN.columnName, fields))
        .leaderboardOptOut(Util.getBooleanFieldValue(fieldValues, Columns.OPT_IN.columnName, fields))
        .promotionsOptOut(Util.getBooleanFieldValue(fieldValues, Columns.OPT_IN.columnName, fields))
        .smsOptOut(Util.getBooleanFieldValue(fieldValues, Columns.SMS_OPT_OUT.columnName, fields))
        .channelsOptOut(Util.getBooleanFieldValue(fieldValues, Columns.OPT_IN.columnName, fields))
        .emailValidated(Util.getBooleanFieldValue(fieldValues, Columns.EMAIL_VALIDATED.columnName, fields))
        .status(Util.getLithiumStatus(status))
        .statusReason(Util.getLithiumStatusReason(status))
        .cellphoneValidated(Util.getBooleanFieldValue(fieldValues, Columns.CELLPHONE_VALIDATED.columnName, fields))
        .build();

    LocalDate dob = Util.getDateFromTimeStampFieldValue(fieldValues, Columns.DATE_OF_BIRTH_1.columnName, fields);
    if (!ObjectUtils.isEmpty(dob)) {
      playerBasic.setDobDay(dob.getDayOfMonth());
      playerBasic.setDobMonth(dob.getMonthValue());
      playerBasic.setDobYear(dob.getYear());
    }
    MigrationPlayerBasic migrationPlayerBasic = mapper.map(playerBasic, MigrationPlayerBasic.class);

    migrationPlayerBasic.setCreatedDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(bdDateValue.longValue()), ZoneOffset.UTC));
    migrationPlayerBasic.setAgeVerified(Util.getBooleanFieldValue(fieldValues, Columns.AGE_VERIFIED.columnName, fields));
    migrationPlayerBasic.setResidentialAddressVerified(Util.getBooleanFieldValue(fieldValues, Columns.ADDRESS_VERIFIED.columnName, fields));
    migrationPlayerBasic.setTestUser(Util.getBooleanFieldValue(fieldValues, Columns.IS_EXCEPTION_CUSTOMER.columnName, fields));
    migrationPlayerBasic.setPasswordHashAlgorithm(Util.getHashingAlgorithmFieldValue(fieldValues, Columns.PASSWORD_HASHING_ALGORITHM.columnName, fields));
    migrationPlayerBasic.setPasswordSalt(Util.getStringFieldValue(fieldValues, Columns.SALT.columnName, fields));

//    //EMAIL & SELF EXCLUSION/TERMINATED
//    if (!ObjectUtils.isEmpty(playerBasic.getEmail()) && playerBasic.getEmail().matches("(_trm_|_se_).*")) {
//      playerBasic.setEmail(Util.removeSelfExclusion(playerBasic.getEmail()));
//    }

    return migrationPlayerBasic;
  }

  public MigrationCredential generateCredentials(FieldValueList fieldValues, FieldList fields) {
    return MigrationCredential.builder()
        .salt(Util.getStringFieldValue(fieldValues, Columns.SALT.columnName, fields))
        .hashedPassword(Util.getStringFieldValue(fieldValues, Columns.HASHED_PASSWORD.columnName, fields))
        .hashingAlgorithm(Util.getHashingAlgorithmFieldValue(fieldValues, Columns.PASSWORD_HASHING_ALGORITHM.columnName, fields))
        .customerId(Util.getStringFieldValue(fieldValues, Columns.USERID.columnName, fields))
        .username(Util.getStringFieldValue(fieldValues, Columns.USERNAME.columnName, fields))
        .securityQuestion(Util.getStringFieldValue(fieldValues, Columns.SECURITY_QUESTION.columnName, fields))
        .securityQuestionAnswer(Util.getStringFieldValue(fieldValues, Columns.SECURITY_ANSWER.columnName, fields))
        .build();
  }
}
