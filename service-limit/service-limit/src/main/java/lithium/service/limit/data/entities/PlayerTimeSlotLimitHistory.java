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
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_plt_player", columnList="playerGuid")
})
public class PlayerTimeSlotLimitHistory {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;

	@Column(nullable=false)
	private long limitFromUtc;

	@Column(nullable=false)
	private long limitToUtc;

	@Column(nullable=false)
	private String playerGuid;

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
