package lithium.service.changelog.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

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
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(
	indexes = {
	@Index(name="idx_changelog_targetbydate", columnList="change_log_entity_id, entityRecordId, changeDate", unique=false),
	@Index(name="idx_changelog_authorbydate", columnList="changeDate, author_user_id", unique=false),
	@Index(name="idx_changelog_entityrecordid", columnList="entityRecordId", unique=false)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChangeLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false, name="change_log_entity_id")
	private ChangeLogEntity entity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false, name="change_log_type_id")
	private ChangeLogType type;

	@Column(nullable=false)
	private long entityRecordId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false, name="author_user_id")
	private User authorUser;

	@Column()
	private String authorFullName;

	@Column(nullable=false)
	private Date changeDate;	

	@Column(length = 65535,columnDefinition="Text")
	private String comments;

	@Column(nullable=false)
	private boolean complete = false;
	
	@Column()
	private String additionalInfo;
	
	@Column()
	private Integer translateFieldChanges;

	@Column()
	private Integer priority = 0;

	public Integer getPriority() {
		return this.priority == null ? 0 : priority;
	}

	@Column()
	private Boolean pinned = false;

	public Boolean getPinned() {
		return this.pinned == null ? false : pinned;
	}

	@Transient
	@JsonManagedReference("ChangeLog")
	private List<ChangeLogFieldChange> fieldChanges;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="category")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sub_category")
	private SubCategory subCategory;

	@Column(nullable=false)
	private boolean deleted;
	@Transient
	private String editedBy;
	@Transient
	private Date dateUpdated;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn()
	private Domain domain;
}

