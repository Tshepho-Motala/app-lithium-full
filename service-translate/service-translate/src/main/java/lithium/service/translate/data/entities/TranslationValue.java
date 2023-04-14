package lithium.service.translate.data.entities;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
		@Index(name="idx_tv_unique", columnList="key_id, language_id", unique=true),
})
@ToString(exclude="key")
@Deprecated
public class TranslationValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private TranslationValueDefault defaultValue;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private TranslationValueRevision current;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private TranslationKey key;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Language language;

	private boolean migrated;
}
