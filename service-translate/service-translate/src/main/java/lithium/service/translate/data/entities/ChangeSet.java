package lithium.service.translate.data.entities;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name = "idx_changeset", columnList = "name, language_id, changeReference", unique = true)
})
public class ChangeSet {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Column(nullable = false)
	private Date applyDate;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String changeReference;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=true)
	private Language language;

	@Column
	private String checksum;

	@Column
	private Date lastUpdated;
}