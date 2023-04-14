package lithium.service.translate.data.entities;

import java.io.Serializable;
import javax.persistence.Cacheable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Data
@Table(indexes = {
		@Index(name="idx_language_enabled", columnList="enabled", unique=false),
		@Index(name="idx_language_locale2", columnList="locale2,enabled", unique=false),
		@Index(name="idx_language_locale3", columnList="locale3,enabled", unique=false)
})

public class Language implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	int version;

	private String locale3;
	private String locale2;
	private String description;
	private boolean enabled;
	
}
