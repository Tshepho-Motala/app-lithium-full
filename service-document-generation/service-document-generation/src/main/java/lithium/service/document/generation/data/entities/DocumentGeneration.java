package lithium.service.document.generation.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lithium.service.document.generation.client.objects.CsvProvider;
import lithium.service.document.generation.converter.CsvProviderEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table()
@ToString(exclude = "parameters")
@EqualsAndHashCode(exclude = "parameters")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentGeneration implements Serializable{

    private static final long serialVersionUID = 7327730340500033719L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Convert(converter = CsvProviderEnumConverter.class)
    private CsvProvider provider;

    private int size;

    private int status;

    private String contentType;

    private Date createdDate;

    private Date completedDate;

    private String authorGuid;

    private String comment;

    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "generation", cascade = CascadeType.MERGE)
    @JsonManagedReference("parameters")
    @Builder.Default
    private List<RequestParameters> parameters = new ArrayList<>();

}
