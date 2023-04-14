package lithium.service.domain.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
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
public class Template implements Serializable {
	private static final long serialVersionUID = -1415354952658363053L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

  @Column(nullable=false)
  private Boolean deleted;

	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String lang;
	
	@Column(nullable=false)
	private Boolean enabled;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	private TemplateRevision current;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private TemplateRevision edit;
	
	@Column(nullable=true)
	private Date editStartedOn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private User editBy;
	
	@PrePersist
	public void prePersist() {
	  if (this.enabled == null) this.enabled = true;
    if (deleted == null) deleted = false;
	}
}
