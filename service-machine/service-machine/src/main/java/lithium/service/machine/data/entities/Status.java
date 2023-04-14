package lithium.service.machine.data.entities;

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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of={"id", "name"})
public class Status implements Serializable {
	private static final long serialVersionUID = -2082517463977013077L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@Column(nullable=false, unique=true)
	@Size(min=2, max=35)
	@Pattern(regexp="^[a-zA-Z0-9_]+$")
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@Column(nullable=false)
	private Boolean enabled;
	
	@Column(nullable=false)
	private Boolean deleted;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "domain_id")
	@JsonManagedReference
	private Domain domain;

}