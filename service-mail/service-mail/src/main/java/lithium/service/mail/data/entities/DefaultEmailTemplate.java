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
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIdentityInfo(generator=ObjectIdGenerators.None.class, property="id")
@Table(indexes = {
		@Index(name = "idx_name", columnList = "name", unique = true)
})
public class DefaultEmailTemplate implements Serializable {
	private static final long serialVersionUID = 1968416892468579458L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String subject;
	
	@Column(nullable=true, length=1000000)
	private String body;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="defaultEmailTemplate", cascade=CascadeType.ALL)
	private List<DefaultEmailTemplatePlaceholder> placeholders = new ArrayList<>();
}