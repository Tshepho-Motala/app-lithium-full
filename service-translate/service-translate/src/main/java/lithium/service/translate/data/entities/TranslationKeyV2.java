package lithium.service.translate.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
	@Index(name="idx_trans_key_v2", columnList="code", unique=true),
}, name = "translation_key_v2")
@ToString(exclude = {"values"})
public class TranslationKeyV2 {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private String description;

	@Column(name = "user_defined")
	private Boolean userDefined;

	@OneToMany(mappedBy="key", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<TranslationValueV2> values;

	@Version
	private int version;

	public void removeValue(TranslationValueV2 value) {
		this.values.remove(value);
	}

	public void addValue(TranslationValueV2 value) {
		this.values.add(value);
	}
}
