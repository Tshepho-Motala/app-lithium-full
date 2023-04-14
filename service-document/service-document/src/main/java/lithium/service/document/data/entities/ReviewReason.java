package lithium.service.document.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import javax.persistence.Version;
import java.io.Serializable;

@Slf4j
@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
        @Index(name = "idx_name_domain", columnList = "name, domain_id", unique = true),
        @Index(name = "idx_enabled", columnList = "enabled", unique = false)
})
public class ReviewReason implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    @JoinColumn(nullable=false)
    @ManyToOne(fetch= FetchType.EAGER)
    private Domain domain;
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;
    @Version
    private int version;

}
