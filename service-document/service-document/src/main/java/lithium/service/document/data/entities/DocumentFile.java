package lithium.service.document.data.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
		@Index(name="idx_df_documentid", columnList="documentId", unique=false)
	})
public class DocumentFile implements Serializable {

	private static final long serialVersionUID = 1L;
	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(nullable=true)
	private Long documentId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private File file;
	
	@Column(nullable=false)	
	private boolean deleted;
	
	@Column(nullable=false)
	private Date uploadDate;

	@Column(nullable = false)
	private int documentPage;
	
	@PrePersist
	void defaults() {
		if (uploadDate == null) uploadDate = new Date();
	}
}
