package lithium.service.access.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_user_guid", columnList="guid", unique=true)
})
public class User implements Serializable {
	private static final long serialVersionUID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;

	@Column(nullable=false)
	private String guid; //is: domain/username

  @Column(name = "test_account", nullable = false)
  private boolean isTestAccount;
	
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
