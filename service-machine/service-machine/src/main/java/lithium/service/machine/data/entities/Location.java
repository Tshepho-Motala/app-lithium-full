package lithium.service.machine.data.entities;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Builder
@ToString(exclude="machine")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="machine")
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = { @Index(columnList="machine_id", unique=false) })
public class Location {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference
	private Machine machine;
	
	@JsonManagedReference
	@ManyToOne(fetch=FetchType.EAGER)
	private LocationDistributionConfiguration distributionConfiguration;
	
	@Column(nullable=true)
	private String entityUuid;
	
	@Transient
	private lithium.service.entity.client.objects.Entity entity;
}
