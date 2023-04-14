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
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@ToString(exclude="job")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="job")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = { 
	@Index(columnList = "job_id", unique = true)
})
public class MachineSettlementProcessingBoundary {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=true)
	private Long lastMachineIdProcessed;
	
	@Column(nullable=true)
	private Date lastDateProcessed;
	
	@Column(nullable=true)
	private Long lastLocationDistConfigRevIdProcessed;
	
	@Column(nullable=true)
	private Long lastRelationshipDistConfigRevIdProcessed;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private MachineSettlement job;
}
