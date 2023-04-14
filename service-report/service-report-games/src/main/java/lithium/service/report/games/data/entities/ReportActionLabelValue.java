package lithium.service.report.games.data.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="reportAction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="reportAction")
@Table(indexes = {
	@Index(name="idx_report_action_id", columnList="report_action_id", unique=false),
	@Index(name="idx_label_value_id", columnList="label_value_id", unique=false)
})
public class ReportActionLabelValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private ReportAction reportAction;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private LabelValue labelValue;
}