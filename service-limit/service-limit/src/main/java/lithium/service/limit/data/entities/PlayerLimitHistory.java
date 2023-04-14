package lithium.service.limit.data.entities;

import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.converter.EnumConverter;
import lithium.service.limit.enums.ModifyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_pl_player_gran_type", columnList="playerGuid, granularity, type", unique=false),
	@Index(name="idx_pl_player", columnList="playerGuid")
})
public class PlayerLimitHistory {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Column(nullable=false)
	private long amount;

	@Column(nullable=false)
	private int granularity;

	@Column(nullable=false)
	private String playerGuid;

	@Column(nullable=false)
	private int type;

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifyTimestamp;

	@Column(nullable=false)
	@Convert(converter=EnumConverter.ModifyTypeConverter.class)
	private ModifyType modifyType;

	@Column(nullable=false)
	private String modifyAuthorGuid;

	@PrePersist
	private void prePersist() {
		if (modifyTimestamp == null) modifyTimestamp = new Date();
	}
}
