package lithium.service.mail.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes={@Index(name="idx_code", columnList="code", unique=true)})
public class Provider implements Serializable {
	private static final long serialVersionUID = -1462973436003667735L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@Column(nullable=false)
	private Boolean enabled;

	@Column(nullable=false)
	private String code;
	
	private String name;
	
	@Column(nullable=false)
	private String url;
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="provider", cascade=CascadeType.ALL)
	private List<ProviderProperty> properties = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private ProviderType providerType;

	@PrePersist
	private void prePersist() {
		if (enabled == null) enabled = true;
	}
}