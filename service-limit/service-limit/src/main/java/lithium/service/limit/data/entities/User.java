package lithium.service.limit.data.entities;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.converter.EnumConverter.LossLimitsVisibilityConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes={
	@Index(name="idx_guid", columnList="guid", unique=true)
})
public class User implements EntityWithUniqueGuid {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	private String guid;

	@Column(name = "test_account", nullable = false)
	private boolean isTestAccount;

	@Column(nullable = true)
	@Convert(converter = LossLimitsVisibilityConverter.class)
	private LossLimitsVisibility lossLimitsVisibility = LossLimitsVisibility.DISABLED;
}
