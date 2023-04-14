package lithium.service.games.data.entities.supplier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierGameMetaResults {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column
  private String payoutLevel;

  @Column
  private Boolean shield;

  @Column
  private String value;

  @Column
  private Integer multiplier;

  @Column
  private String location;

  @Column
  private String color;

  @Column
  private String score;

  @Column
  private String ties;

  @Column
  private Boolean playerPair;

  @Column
  private Boolean bankerPair;

  @Column(name = "\"natural\"")
  private Boolean natural;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "supplier_game_meta_results_id")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<SupplierGameMetaResults> results = new ArrayList<>();
}
