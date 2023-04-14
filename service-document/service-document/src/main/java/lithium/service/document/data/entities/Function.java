package lithium.service.document.data.entities;

import java.io.Serializable;

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

@Slf4j
@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
		@Index(name="idx_func_name_authorservice", columnList="name, author_service_id", unique=true)
	})

/**
 * The implementing author service can provide a document type description ( document function )
 * to assist in filtering relevant documents for the document owner.
 * 
 */
@Deprecated
public class Function implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private AuthorService authorService;
}