package lithium.service.mail.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import java.io.Serializable;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
	indexes = {
		@Index(name = "idx_domain_deleted_priority", columnList = "domain_id, deleted, priority"),
		@Index(name = "idx_domain_deleted_enabled_priority", columnList = "domain_id, deleted, enabled, priority")
	}
)
public class DomainProvider implements Serializable {
	private static final long serialVersionUID = -1636608428683591430L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Provider provider;
	
	@Column(nullable=false)
	private Boolean enabled;
	
	@Column(nullable=false)
	private Boolean deleted;
	
	@Column(nullable=true)
	private String accessRule;
	
	@Column(nullable=false)
	private Integer priority;
	
	@PrePersist 
	private void prePersist() {
		if (priority == null) priority = 999;
	}
}