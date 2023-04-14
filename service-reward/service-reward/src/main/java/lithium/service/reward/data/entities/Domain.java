package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "domain", indexes = {@Index( name = "idx_domain_name", columnList = "name", unique = true ),} )
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class Domain implements Serializable, EntityWithUniqueName {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @Version
  int version;

  @Column( nullable = false )
  private String name;
}
