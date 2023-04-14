package lithium.service.changelog.data.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table
@ToString(exclude="changeLog")
@EqualsAndHashCode(exclude="changeLog")
public class ChangeLogFieldChange {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	@JsonBackReference("ChangeLog")
	private ChangeLog changeLog;

	@Column(nullable=false)
	private String field;

	@Column(nullable=true, length=2000)
	private String fromValue;

	@Column(nullable=true, length=2000)
	private String toValue;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false, name="edited_by")
	private User editedBy;

	@Column
	private Date dateUpdated;
	@Transient
	private String editorFullName;

}

