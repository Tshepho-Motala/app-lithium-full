package lithium.service.document.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
@Deprecated
@Slf4j
@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
		@Index(name="idx_doc_uuid", columnList="uuid", unique=true),
		@Index(name="idx_doc_owner_authorservice", columnList="owner_id, author_service_id", unique=false)
	})
public class Document implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	
	private String uuid;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Status status;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Function function;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Owner owner;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private AuthorService authorService;
	
	private boolean deleted;
	
	private boolean archived;
	
	private Date lastFileUploadDate;

	private boolean migrated;


}