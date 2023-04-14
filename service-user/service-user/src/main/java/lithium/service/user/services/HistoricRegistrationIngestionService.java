package lithium.service.user.services;

import java.time.ZoneOffset;
import java.util.Date;
import javax.validation.Valid;
import lithium.exceptions.Status412DomainNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.libraryvbmigration.data.dto.MigrationPlayerBasic;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.User;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.UserPasswordHashAlgorithm;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.enums.PasswordHashAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HistoricRegistrationIngestionService {
  private final AddressRepository addressRepository;
  private final DomainService domainService;
  private final ModelMapper modelMapper;
  private final StatusRepository statusRepository;
  private final UserService userService;
  private final SignupService signupService;
  private final CollectionDataService collectionDataService;
  private final MessageSource messageSource;
  private final UserPasswordHashAlgorithmService userPasswordHashAlgorithmService;

  @Transactional
  public User createBasicUser(@Valid MigrationPlayerBasic pb)
      throws Status412DomainNotFoundException, Status550ServiceDomainClientException,
      Status426InvalidParameterProvidedException, Status500InternalServerErrorException, IllegalAccessException {

    //If the player basic contains collectionData it needs to be validated
    collectionDataService.validateCollectionDataInput(pb);

    Domain domain = domainService.findOrCreate(pb.getDomainName());

    Address residentialAddress = null;
    if (!ObjectUtils.isEmpty(pb.getResidentialAddress())) {
      residentialAddress = signupService.validateAndOverrideGeoData(pb.getResidentialAddress(), "residentialAddress", pb.getDomainName());
    }

    Status status = statusRepository.findByName(lithium.service.user.client.enums.Status.OPEN.statusName());

    lithium.service.user.data.entities.User user = userService.buildUser(pb, domain, residentialAddress, null, status, null,
        pb.getPassword());

    if (!ObjectUtils.isEmpty(residentialAddress)) {
      residentialAddress.setUserId(user.getId());
      addressRepository.save(residentialAddress);
    }

    try {
      user.setCreatedDate(Date.from(pb.getCreatedDate().atZone(ZoneOffset.UTC).toInstant()));
    } catch (Exception e) {
      throw new Status426InvalidParameterProvidedException(
          RegistrationError.INVALID_REGISTRATION_DATE.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[]{pb.getCreatedDate()}));
    }

    try {
      user.setEmailValidated(pb.getEmailValidated());
      user.setAgeVerified(pb.isAgeVerified());
      user.setTestAccount(pb.isTestUser());
      user.setCellphoneValidated(pb.getCellphoneValidated());
      user.setAddressVerified(pb.isResidentialAddressVerified());
      user.setCallOptOut(pb.isCallOptOut());
      user.setPostOptOut(pb.isPostOptOut());
      user.setSmsOptOut(pb.isSmsOptOut());
      user.setEmailOptOut(pb.isEmailOptOut());
      user = userService.save(user, true);

      userPasswordHashAlgorithmService.save(user, pb.getPasswordSalt(), PasswordHashAlgorithm.fromId(pb.getPasswordHashAlgorithm()));
    } catch (Exception e) {
      throw new Status500InternalServerErrorException("An error occurred while saving user: " + e.getMessage(), e);
    }

    return modelMapper.map(user, lithium.service.user.client.objects.User.class);
  }
}
