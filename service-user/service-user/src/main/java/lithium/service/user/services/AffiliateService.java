package lithium.service.user.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.LabelValue;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserRevision;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.objects.LabelValueDTO;
import lithium.service.user.data.repositories.LabelValueRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class AffiliateService {

  final LabelValueRepository labelValueRepository;
  final UserLabelValueService userLabelValueService;
  final ModelMapper modelMapper;
  final LithiumServiceClientFactory services;

  @Autowired
  private LabelService labelService;

  public AffiliateService(
      LabelValueRepository labelValueRepository,
      UserLabelValueService userLabelValueService,
      ModelMapper modelMapper,
      LithiumServiceClientFactory services) {
    this.labelValueRepository = labelValueRepository;
    this.userLabelValueService = userLabelValueService;
    this.modelMapper = modelMapper;
    this.services = services;
  }


  public void registerAffiliatePlayer(PlayerBasic pb, final User u) {
    UserRevision repoUserRevision = u.getCurrent();
    HashMap<String, String> labelAndValueMap = new HashMap<>();

    if (repoUserRevision != null && repoUserRevision.getLabelValueList() != null && !repoUserRevision.getLabelValueList().isEmpty()) {
      for (UserRevisionLabelValue lv : repoUserRevision.getLabelValueList()) {
        labelAndValueMap.put(lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
      }
    }

    labelToMap(labelAndValueMap, Label.AFFILIATE_GUID_LABEL, pb.getAffiliateGuid());
    labelToMap(labelAndValueMap, Label.AFFILIATE_SECONDARY_GUID_1_LABEL, pb.getAffiliateSecondaryGuid1());
    labelToMap(labelAndValueMap, Label.AFFILIATE_SECONDARY_GUID_2_LABEL, pb.getAffiliateSecondaryGuid2());
    labelToMap(labelAndValueMap, Label.AFFILIATE_SECONDARY_GUID_3_LABEL, pb.getAffiliateSecondaryGuid3());

    Map<String, String> additionalData = pb.getAdditionalData() != null ? pb.getAdditionalData() : new HashMap<>();
    if (!labelAndValueMap.isEmpty()) {
      labelAndValueMap.forEach((label, value) -> {
        additionalData.put(label, value);
      });
    }
    pb.setAdditionalData(additionalData);
  }

  /**
   * @param user
   * @return
   */
  public HashMap<String, String> requestAffiliatePlayer(final User user) {
    UserRevision userRevision = user.getCurrent();
    HashMap<String, String> result = new HashMap<>();

    if (userRevision != null && userRevision.getLabelValueList() != null && !userRevision.getLabelValueList().isEmpty()) {
      for (UserRevisionLabelValue lv : userRevision.getLabelValueList()) {
        result.put(lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
      }
    }

    return result;
  }

  public HashMap<String, String> getAffiliateLabelValues(final User user) {
    HashMap affiliateMap = new HashMap();
    if(!ObjectUtils.isEmpty(user.getCurrent()) && !ObjectUtils.isEmpty(user.getCurrent().getLabelValueList())) {
      List<UserRevisionLabelValue> userRevisionLabelValues = user.getCurrent().getLabelValueList().stream()
          .filter(s -> s.getLabelValue().getLabel().getName().equals(Label.AFFILIATE_GUID_LABEL)
              || s.getLabelValue().getLabel().getName().equals(Label.AFFILIATE_SECONDARY_GUID_1_LABEL)
              || s.getLabelValue().getLabel().getName().equals(Label.AFFILIATE_SECONDARY_GUID_2_LABEL)
              || s.getLabelValue().getLabel().getName().equals(Label.AFFILIATE_SECONDARY_GUID_3_LABEL)).toList();
      for(UserRevisionLabelValue labelValues : userRevisionLabelValues) {
        affiliateMap.put(labelValues.getLabelValue().getLabel().getName(), labelValues.getLabelValue().getValue());
      }
    }
    return affiliateMap;
  }

  private void labelToMap(HashMap<String, String> map, String labelName, String value) {
    if (value != null && !value.trim().isEmpty()) {
      map.put(labelName, value);
    }
  }

  public <E> Optional<E> getClient(Class<E> theClass, String url) {
    E clientInstance = null;

    try {
      clientInstance = services.target(theClass, url, true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error(e.getMessage(), e);
    }
    return Optional.ofNullable(clientInstance);

  }

  @Cacheable(value = "lithium.service.user.Label.common.affiliates.guids", unless = "#result == null")
  public List<lithium.service.user.data.entities.Label> getCommonAffiliateLabels() {
    return labelService.getLabelsByNames(Label.COMMON_AFFILIATE_GUID_LABEL_NAMES);
  }

  public Page<LabelValueDTO> findAffiliatesByName(String name, Pageable page) {

    List<lithium.service.user.data.entities.Label> affiliateLabels = getCommonAffiliateLabels();

    Page<LabelValue> matchingLabels;
    if (name == null) {
      matchingLabels = labelValueRepository.findAllByLabelIn(affiliateLabels, page);
    } else {
      name = name + "%";
      matchingLabels = labelValueRepository.findAllByLabelInAndAndValueLike(affiliateLabels, name, page);
    }

    return matchingLabels
        .map(labelValue -> new LabelValueDTO(labelValue.getId(), labelValue.getValue()));

  }
}
