package lithium.service.user.services;

import java.util.List;
import java.util.stream.Collectors;
import lithium.leader.LeaderCandidate;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.repositories.AddressRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RegistrationSynchronizeService {

  private static final String COUNTRY_LABEL = "country";
  private static final String CITY_LABEL = "city";
  private static final String ADMIN1_LABEL = "adminLevel1";
  private static final Integer PAGE = 0;

  @Autowired
  ServiceUserConfigurationProperties userConfigProperties;
  @Autowired
  LithiumServiceClientFactory lithiumServiceClientFactory;
  @Autowired
  AddressRepository addressRepository;
  @Autowired
  LeaderCandidate leaderCandidate;

  public Address overrideGeoData(Address address) {
    try {
      if(address != null) {
        if(userConfigProperties.getOverrideGeoData() != null && !userConfigProperties.getOverrideGeoData().isEmpty()) {
          userConfigProperties.getOverrideGeoData().forEach((k, v) -> {
            String[] keySplit = k.split("\\|");
            if(keySplit.length >= 2) {
              switch (keySplit[0]) {
                case COUNTRY_LABEL:
                  if(address.getCountryCode() != null && address.getCountryCode().equalsIgnoreCase(keySplit[1])) {
                    if(address.getCountry() == null || (address.getCountry() != null && !address.getCountry().equalsIgnoreCase(v))) {
                      address.setCountry(v);
                    }
                  }
                  break;
                case ADMIN1_LABEL:
                  if(address.getAdminLevel1Code() != null && address.getAdminLevel1Code().equalsIgnoreCase(keySplit[1])) {
                    if(address.getAdminLevel1() == null || (address.getAdminLevel1() != null && !address.getCountry().equalsIgnoreCase(v))) {
                      address.setAdminLevel1(v);
                    }
                  }
                  break;
                case CITY_LABEL:
                  if(address.getCityCode() != null && address.getCityCode().equalsIgnoreCase(keySplit[1])) {
                    if(address.getCity() == null || (address.getCity() != null && !address.getCity().equalsIgnoreCase(v))) {
                      address.setCity(v);
                    }
                  }
                  break;
              }
            }
          });
        }
      }
    } catch (Exception ex) {
      log.error("overrideGeoData : " + ex.getMessage(), ex.getStackTrace());
    }
    return address;
  }

  @Scheduled(fixedRateString = "${lithium.services.user.jobs.geo-names-update.fixedRate-in-milliseconds}")
  public void overrideCountryData() {
    try {

      if(!userConfigProperties.getJobs().getGeoNamesUpdate().isEnabled()) {
        return;
      }

      if (!leaderCandidate.iAmTheLeader()) {
        log.debug("I am not the leader.");
        return;
      }

      log.info("Cron overrideCountryData has started ......!");

      if(userConfigProperties.getOverrideGeoData() != null && !userConfigProperties.getOverrideGeoData().isEmpty()) {
        userConfigProperties.getOverrideGeoData().entrySet().stream().sequential().forEach( k -> {
          String[] keySplit = k.getKey().split("\\|");
          if(keySplit.length >= 2) {
            switch(keySplit[0]) {
              case COUNTRY_LABEL:
                PageRequest pageRequest = PageRequest.of(PAGE, userConfigProperties.getJobs().getGeoNamesUpdate().getPageSize());
                Page<Address> byCountry = addressRepository.findByCountryCodeAndCountryNot(keySplit[1],
                    k.getValue(), pageRequest);
                if(byCountry.getTotalElements() > 0) {
                  List<Address> modifiedAddressList = byCountry.getContent().stream().map(m -> {
                    m.setCountry(k.getValue());
                    return m;
                  }).collect(Collectors.toList());
                  addressRepository.saveAll(modifiedAddressList);
                }
                break;
              case CITY_LABEL:
                pageRequest = PageRequest.of(PAGE, userConfigProperties.getJobs().getGeoNamesUpdate().getPageSize());
                Page<Address> byCityCode = addressRepository.findByCityCodeAndCityNot(keySplit[1], k.getValue(), pageRequest);
                if(byCityCode.getTotalElements() > 0) {
                  List<Address> modifiedAddressList = byCityCode.getContent().stream().map(a -> {
                    a.setCity(k.getValue());
                    return a;
                  }).collect(Collectors.toList());
                  addressRepository.saveAll(modifiedAddressList);
                }
                break;
              case ADMIN1_LABEL:
                pageRequest = PageRequest.of(PAGE, userConfigProperties.getJobs().getGeoNamesUpdate().getPageSize());
                Page<Address> byAdminLevel1Code = addressRepository.findByAdminLevel1CodeAndAdminLevel1Not(keySplit[1], k.getValue(), pageRequest);
                if(byAdminLevel1Code.getTotalElements() > 0) {
                  List<Address> modifiedAddressList = byAdminLevel1Code.getContent().stream().map(a -> {
                    a.setAdminLevel1(k.getValue());
                    return a;
                  }).collect(Collectors.toList());
                  addressRepository.saveAll(modifiedAddressList);
                }
                break;
            }
          }
        });
      }
      log.info("Cron overrideCountryData has finished ......!");
    } catch (Exception ex) {
      log.error("Cron overrideCountryData : " + ex.getMessage(), ex.getStackTrace());
    }
  }
}
