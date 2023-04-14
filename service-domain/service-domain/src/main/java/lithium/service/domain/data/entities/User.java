package lithium.service.domain.data.entities;

import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@ToString
@EqualsAndHashCode
@Table(indexes = {
	@Index(name="idx_user_guid", columnList="guid", unique=true),
})
public class User implements Serializable {
	private static final long serialVersionUID = 1304084908675930632L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String guid;

  @Column(name = "test_account", nullable = false)
  private boolean isTestAccount;
}
