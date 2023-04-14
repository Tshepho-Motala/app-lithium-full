package lithium.service.document.generation.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@ToString
@Table()
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestParameters {

    private static final long serialVersionUID = 7327730340500033719L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="parameter_key", nullable=false)
    private String key;

    @Column(name="parameter_value")
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generation_id",nullable = false)
    @JsonBackReference("generation")
    private DocumentGeneration generation;
}
