package lithium.service.document.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "document_v2", indexes = {
        @Index(name = "idx_owner_document_file", columnList = "owner_id, `document_file_id`", unique = true),
})
public class DocumentV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Domain domain;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Owner owner;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DocumentType documentType;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ReviewStatus reviewStatus;

    @ManyToOne
    @JoinColumn(nullable = true)
    private ReviewReason reviewReason;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DocumentFile documentFile;

    @Column
    private String fileName;

    @Column(name = "`sensitive`")
    private boolean sensitive;

    private boolean deleted;

    @Version
    private int version;
}
