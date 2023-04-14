package lithium.service.pushmsg.data.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@JsonIgnoreProperties("pushMsgTemplate")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property="id")
public class PushMsgTemplateRevision implements Serializable {
	private static final long serialVersionUID = 1323212566974168347L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=true)
	private String description;
	
	@Column(nullable=true)
	private String providerTemplateId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private PushMsgTemplate pushMsgTemplate;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private List<PushMsgHeading> pushMsgHeadings;
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	private List<PushMsgContent> pushMsgContents;
}