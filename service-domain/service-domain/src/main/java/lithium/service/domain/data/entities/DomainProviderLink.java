package lithium.service.domain.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DomainProviderLink implements Serializable {
	private static final long serialVersionUID = -2627674110789559624L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Provider provider;

	@Column(nullable=false)
	private Boolean enabled;

	@Column(nullable=false)
	private Boolean deleted;
	
	@Column(nullable=false)
	private Boolean ownerLink;
}