package lithium.service.translate.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
//		@Index(name="idx_lottery_templates", columnList="template", unique=false),
//		@Index(name="idx_lottery_archived", columnList="archived", unique=false),
})
@ToString
@EntityListeners(AuditingEntityListener.class)
@Deprecated
public class TranslationValueDefault {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private Date createdDate;
	
	@Column(nullable=false, length=2000)
	private String value;
	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(nullable=true)
//	@JsonIgnore
//	private TranslationValue translationValue;
	private Long translationValueId;

}
