package lithium.service.libraryvbmigration.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MigrationExceptionRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Column(length = 5000)
  private String requestJson;
  @Column(length = 5000)
  private String exceptionMessage;
  @Column
  private String customerId;
  @Enumerated(EnumType.STRING)
  private MigrationType migrationType;

}
