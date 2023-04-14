package lithium.service.document.generation.data.entities;

import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.converter.CsvProviderEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Slf4j
@Entity
@Data
@ToString(exclude="data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name="idx_doc_reference", columnList="reference", unique=false),
        @Index(name="idx_doc_created_date", columnList="created_date", unique=false),
})
public class DocumentFile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String reference;
    @Convert(converter = CsvProviderEnumConverter.class)
    private CsvProvider provider;
    @Lob
    private byte[] data;
    @Column(name="created_date", nullable=false)
    private Date createdDate;
}
