package lithium.service.mail.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="defaultEmailTemplate")
@JsonIgnoreProperties("defaultEmailTemplate")
@EqualsAndHashCode(exclude="defaultEmailTemplate")
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(
		indexes = {
				@Index(name = "idx_default_template_and_name", columnList = "default_email_template_id,name", unique = true),
		}
)

public class DefaultEmailTemplatePlaceholder implements Serializable {
	private static final long serialVersionUID = 5815736110417158980L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private DefaultEmailTemplate defaultEmailTemplate;
}