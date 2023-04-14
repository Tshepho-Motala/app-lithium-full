package lithium.service.sms.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="provider")
@JsonIgnoreProperties("provider")
@EqualsAndHashCode(exclude="provider")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ProviderProperty implements Serializable {
	private static final long serialVersionUID = 8819641832905779435L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Provider provider;
	
	@NonNull
	@Column(nullable=false, unique=false)
	private String name;
	
	@Column(unique=false)
	private String defaultValue;
	
	@Column(nullable=false)
	private String type;
	
	@Column(nullable=false)
	private String description;
}