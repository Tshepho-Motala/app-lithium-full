package lithium.service.promo.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
		@Index(name="idx_guid", columnList="guid", unique=true)
})
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = -4961988316574954566L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@Column(nullable=false)
	private String guid;

	@Column(name = "test_account", nullable = false)
	private boolean isTestAccount;

	@Builder.Default
	private String timezone = ZoneId.systemDefault().getId();

	/// Utility methods
	public String domainName() {
		return guid.split("/")[0];
	}
	public String username() {
		return guid.split("/")[1];
	}
	public String guid() {
		return guid;
	}
}
