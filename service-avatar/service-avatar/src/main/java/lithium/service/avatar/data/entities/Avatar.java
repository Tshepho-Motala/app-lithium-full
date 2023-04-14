package lithium.service.avatar.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="graphic")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="graphic")
@Table
public class Avatar {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@Column(nullable=false)
	private Boolean enabled;
	
	@Column(nullable=false)
	private Boolean isDefault;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Graphic graphic;
	
	@PrePersist
	public void prePersist() {
		if (enabled == null) enabled = false;
		if (isDefault == null) isDefault = false;
	}
}