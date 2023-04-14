package lithium.service.machine.data.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@ToString(exclude="relationship")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="relationship")
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = { @Index(columnList="relationship_id", unique=false) })
public class RelationshipDistributionConfiguration {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonBackReference
	private Relationship relationship;
	
	@JsonManagedReference
	@ManyToOne(fetch=FetchType.EAGER)
	private RelationshipDistributionConfigurationRevision current;
}
