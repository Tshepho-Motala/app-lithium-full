package lithium.service.cdn.cms.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsAsset {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable=false)
  private Boolean deleted;

  @Version
  private int version;

  @ManyToOne(fetch= FetchType.EAGER)
  @JoinColumn(nullable=false)
  private Domain domain;

  @Column(nullable=false)
  private String name;

  @Column(nullable=false)
  private String url;

  @Column(nullable=false)
  private String type;

  @Column(nullable = false)
  private Date uploadedDate;

  @Column(nullable = false)
  private String size;

  @PrePersist
  public void prePersist() {
    if(uploadedDate == null) uploadedDate = new Date();

    if (deleted == null) deleted = false;
  }
}
