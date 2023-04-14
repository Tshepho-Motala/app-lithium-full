package lithium.service.notifications.services;

import lithium.service.notifications.data.entities.Label;
import lithium.service.notifications.data.entities.LabelValue;
import lithium.service.notifications.data.repositories.LabelValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelValueService {

  @Autowired
  LabelService labelService;

  @Autowired
  LabelValueRepository labelValueRepository;

  public LabelValue findOrCreate(String labelName, String value) {
    Label l = labelService.findOrCreate(labelName);
    LabelValue o = labelValueRepository.findByLabelAndValue(l, value);
    if (o != null) {
      return o;
    }
    return labelValueRepository.save(LabelValue.builder().label(l).value(value).build());
  }
}
