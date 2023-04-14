package lithium.service.machine.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString(exclude="boundary")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="boundary")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = { @Index(name="idx_batch_name", columnList="batchName", unique=true) })
public class MachineSettlement {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String batchName;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private Date dateStart;
	
	@Column(nullable=false)
	private Date dateEnd;
	
	@Column(nullable=false)
	private String createdBy;
	
	@Column(nullable=false)
	private Boolean processing;
	
	@Column(nullable=false)
	private Boolean completed;
	
	@Column(nullable=true)
	private Date startedOn;
	
	@Column(nullable=false)
	private Date lastUpdated;
	
	@Column(nullable=true, length=1000000)
	private String lastFailedReason;
	
	@Column(nullable=true)
	private Date lastFailedDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonManagedReference
	private MachineSettlementProcessingBoundary boundary;
	
	@Column(nullable=false)
	private Boolean rerun;
	
	@PrePersist()
	public void prePersist() {
		if (processing == null) processing = false;
		if (completed == null) completed = false;
		if (lastUpdated == null) lastUpdated = new Date();
		if (rerun == null) rerun = false;
	}
}
