package lithium.service.stats.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lithium.service.stats.client.objects.Period.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StatSummary implements Serializable {
	private static final long serialVersionUID = 8134383611926872959L;
	private Long id;
	private Stat stat;
	private Period period;
	@Default
	private Long count = 0L;
	@Default
	private List<LabelValue> labelValues = new ArrayList<>();
	
	public String labelsToString() {
		String lvs = "[";
		for (LabelValue lv:labelValues) {
			lvs += lv.getLabel().getName()+" = "+lv.getValue()+", ";
		}
		lvs += "]";
		return lvs;
	}

	public String toShortString() {
		return "StatSummary [id=" + id + ", stat=" + stat.getName() + ", period=" + Granularity.fromGranularity(period.getGranularity()) + ", count="
				+ count + ", labelValues=" + labelsToString() + "]";
	}
}