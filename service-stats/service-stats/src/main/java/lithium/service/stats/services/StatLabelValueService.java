package lithium.service.stats.services;

import lithium.service.stats.data.entities.Label;
import lithium.service.stats.data.entities.LabelValue;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatLabelValue;
import lithium.service.stats.data.repositories.StatLabelValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatLabelValueService {
	@Autowired private LabelService labelService;
	@Autowired private LabelValueService labelValueService;
	@Autowired private StatLabelValueRepository statLabelValueRepository;

	public StatLabelValue findStatLabelValue(Stat stat, String labelName) {
		Label label = labelService.findOrCreate(labelName);
		return findStatLabelValue(stat, label);
	}

	private StatLabelValue findStatLabelValue(Stat stat, Label label) {
		return statLabelValueRepository.findByLabelValueLabelAndStat(label, stat);
	}

	public StatLabelValue updateOrCreateStatLabelValue(Stat stat, String labelName, String value) {
		Label label = labelService.findOrCreate(labelName);
		LabelValue labelValue = labelValueService.findOrCreate(label, value);
		StatLabelValue statLabelValue = findStatLabelValue(stat, label);

		if (statLabelValue == null) {
			statLabelValue = statLabelValueRepository.save(
				StatLabelValue.builder()
				.labelValue(labelValue)
				.stat(stat)
				.build()
			);
		} else {
			statLabelValue.setLabelValue(labelValue);
			statLabelValue = statLabelValueRepository.save(statLabelValue);
		}

		return statLabelValue;
	}
}
