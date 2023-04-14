package lithium.service.machine.data.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Builder
@ToString(exclude="relationshipDistributionConfiguration")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="relationshipDistributionConfiguration")
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = { @Index(columnList="relationship_distribution_configuration_id", unique=false) })
public class RelationshipDistributionConfigurationRevision {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private BigDecimal percentage;
	
	@Column(nullable=false)
	private Date start;
	
	@Column(nullable=true)
	private Date end;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonBackReference
	private RelationshipDistributionConfiguration relationshipDistributionConfiguration;
	
	@Transient
	private lithium.service.entity.client.objects.Entity entity;
	
	@PrePersist
	public void prePersist() {
		if (start == null) start = new Date();
	}
}
