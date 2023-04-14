package lithium.service.document.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
import java.io.Serializable;

import static java.util.Objects.nonNull;

@Slf4j
@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_name", columnList = "name", unique = false),
        @Index(name = "idx_document_type_name_unique", columnList = "document_type_id, name", unique = true)
})
public class DocumentTypeMappingName implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private DocumentType documentType;

    @Column(nullable = false)
    private String name;

    @Override
    public String toString() {
        return new StringBuilder("\"")
                .append(name).append("\"(")
                .append(id).append(")")
                .toString();
    }
}
