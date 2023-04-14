package lithium.service.access.provider.sphonic.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	indexes = {
		@Index(name = "idx_domain", columnList = "domain_id", unique = true)
	}
)
public class Authentication implements Serializable {
	private static final long serialVersionUID = 8350364808179795538L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne
	@JoinColumn
	private Domain domain;

	@Lob
	@Column(nullable = false)
	private String accessToken;

	@Column(nullable = false)
	private Date expirationDate;
}
