package lithium.service.document.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lithium.service.document.client.objects.DocumentPurpose;
import lithium.service.document.data.converter.EnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Data
@ToString(exclude = {"iconBase64"})
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(indexes = {
        @Index(name="idx_domain_purpose_type", columnList="domain_id, purpose, type", unique=true)
})
public class DocumentType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    @Convert(converter = EnumConverter.DocumentPurposeConverter.class)
    private DocumentPurpose purpose;
    @Column(nullable = false)
    private String type;
    @JoinColumn(nullable=false)
    @ManyToOne(fetch=FetchType.EAGER)
    private Domain domain;
    @Lob
    private byte[] iconBase64;
    @Column
    private String iconName;
    @Column
    private String iconType;
    @Column
    private Long iconSize;
    @Column
    private boolean enabled;
    @Column(nullable = false)
    @LastModifiedDate
    private Long modifiedDate;
    @Version
    private int version;
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy="documentType")
    @Builder.Default
    private List<DocumentTypeMappingName> mappingNames = new ArrayList<>();
    @Column
    private boolean typeSensitive;
}
