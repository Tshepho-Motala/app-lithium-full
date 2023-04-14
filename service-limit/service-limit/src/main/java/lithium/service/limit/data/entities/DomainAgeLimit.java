package lithium.service.limit.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Version;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
        @Index(name = "idx_dal_domain_gran_type_age", columnList = "domainName, granularity, type, ageMin, ageMax", unique = true),
        @Index(name = "idx_dal_domain", columnList = "domainName")
})
public class DomainAgeLimit implements Serializable {
    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @Column(nullable=false)
    private String domainName;

    @Column(nullable=false)
    private int granularity;

    @Column(nullable=false)
    private int ageMax;

    @Column(nullable=false)
    private int ageMin;

    @Column(nullable=false)
    private long amount;

    @Column(nullable=false)
    private int type;

    @Builder.Default
    @Column(nullable=false)
    @CreatedDate
    private Date createdDate = new Date();

    @Builder.Default
    @Column(nullable=false)
    @LastModifiedDate
    private Date modifiedDate = new Date();

    @Column(nullable=false)
    private String createdByGuid;

    @Column(nullable=false)
    private String modifiedByGuid;
}
