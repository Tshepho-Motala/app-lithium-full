package lithium.service.domain.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@Entity
@Builder
@ToString(exclude="provider")
@EqualsAndHashCode(exclude="provider")
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@JsonIgnoreProperties("provider")
public class ProviderProperty implements Serializable {

	private static final long serialVersionUID = 5013379007077889150L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@JsonBackReference("provider")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Provider provider;

	@Column(nullable=false, unique=false)
	private String name;

	@Column(nullable=false, unique=false)
	private String value;
}
