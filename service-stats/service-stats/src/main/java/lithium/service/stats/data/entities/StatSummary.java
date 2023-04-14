package lithium.service.stats.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StatSummary implements Serializable {
	private static final long serialVersionUID = -2747314429756233145L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Stat stat;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;
	
	@Default
	private Long count = 0L;

	public void incrementCount() {
		this.count++;
	}

	@Default
	@Transient
	private boolean updating = false;
	
	@Default
	@Transient
	private List<LabelValue> labelValues = new ArrayList<>();
	
	public void addLabelValue(LabelValue labelValue) {
		if (labelValues == null) labelValues = new ArrayList<>();
		labelValues.add(labelValue);
	}
	
	public String labelsToString() {
		String lvs = "[";
		if (labelValues != null) {
			for (LabelValue lv: labelValues) {
				if (lvs.length() > 1) lvs += ", ";
				lvs += lv.getLabel().getName() + " = " + lv.getValue();
			}
		}
		lvs += "]";
		return lvs;
	}

	public String toShortString() {
		return "StatSummary [id=" + id + ", version=" + version + ", stat=" + stat.getName() + ", period=" + period.granularity() + ", count="
				+ count + ", labelValues=" + labelsToString() + "]";
	}
}
