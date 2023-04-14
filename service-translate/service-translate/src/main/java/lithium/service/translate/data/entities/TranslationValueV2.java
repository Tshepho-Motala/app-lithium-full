package lithium.service.translate.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
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
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
		@Index(name="idx_trans_value_v2", columnList="key_id, language_id, domain_id", unique=true),
}, name = "translation_value_v2")
public class TranslationValueV2 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;

	@Column(nullable = false, columnDefinition = "longtext")
	private String value;

	@Column(nullable = true)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	@JsonManagedReference
	private TranslationKeyV2 key;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=true)
	private Language language;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Domain domain;

	@Column(nullable = false)
	private Date lastUpdated;

	@PrePersist
	private void prePersist() {
		lastUpdated = new Date();
	}
}
