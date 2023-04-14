package lithium.service.casino.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Version;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotApiDataMigration {
	@Id
	private Long id;

	@Version
	private int version;

	@Column
	private Long currentId;

	@Column(nullable = false)
	private boolean processing;
}
