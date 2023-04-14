package lithium.service.user.services;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.transaction.Transactional;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.IncompleteUserLabelValue;
import lithium.service.user.data.repositories.IncompleteUserLabelValueRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncompleteUserLabelValueService {
  @Autowired LabelValueService labelValueService;
  @Autowired IncompleteUserRepository incompleteUserRepository;
  @Autowired IncompleteUserLabelValueRepository incompleteUserLabelValueRepository;

  @Transactional
  public IncompleteUser updateOrAddUserLabelValues(IncompleteUser icu, Map<String,String> labelAndValueMap) {

    for (Entry<String, String> entry : labelAndValueMap.entrySet()) {
      String label = entry.getKey();
      String value = entry.getValue();

      Optional<IncompleteUserLabelValue> labelValue = incompleteUserLabelValueRepository
          .findByIncompleteUserAndLabelValueLabelName(icu, label);
      if (labelValue.isPresent()) {
        IncompleteUserLabelValue lv = labelValue.get();
        lv.getLabelValue().setValue(value);
        incompleteUserLabelValueRepository.save(lv);
      } else {
        incompleteUserLabelValueRepository.save(
            IncompleteUserLabelValue.builder()
                .incompleteUser(icu)
                .labelValue(labelValueService.findOrCreate(label, value))
                .build()
        );
      }
    }
    return incompleteUserRepository.findOne(icu.getId());
  }
}
