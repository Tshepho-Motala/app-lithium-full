package lithium.service.xp.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Data
@ToString(exclude="level")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="level")
@Table
public class LevelNotification {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@JsonBackReference("level")
	@ManyToOne(fetch= FetchType.EAGER)
	private Level level;

	@Column(nullable=false)
	private Long triggerPercentage;

	@Column(nullable=false)
	private String notificationName;
}
