package lithium.service.games.data.entities.supplier;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lithium.service.games.data.entities.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Data
@ToString(exclude = "game")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "supplier_game_meta_data",
    indexes = {
        @Index(name = "idx_supplier_game_guid", columnList = "")
    }
)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class SupplierGameMetaData  implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String supplierGameGuid;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "game_vertical_id")
  private SupplierGameMetaVertical gameVertical;

  private String gameType;

  private String gameSubType;

  private String name;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "supplier_game_meta_data_id")
  private Set<SupplierGameMetaDescription> descriptions = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "display_id")
  private SupplierGameMetaDisplay display;

  @Column
  private Boolean open;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "supplier_game_meta_data_id")
  private List<SupplierGameMetaBetLimits> betLimits = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "dealer_id")
  private SupplierGameMetaDealer dealer;

  @Column
  private Integer players;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "operation_hours_id")
  private SupplierGameMetaHours operationHours;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "supplier_game_meta_data_id")
  private List<SupplierGameMetaLinks> links = new ArrayList<>();

  @Column
  private Integer seats;

  @Column
  private Boolean betBehind;

  @Column
  private String seatsTaken;

  @Column
  private String dealerHand;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "supplier_game_meta_data_id")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<SupplierGameMetaResults> results = new ArrayList<>();

  @Column(name = "history", columnDefinition = "LONGTEXT")
  private String history;

  @Transient
  @JsonProperty("gameID")
  private String gameGuid;

  @OneToOne(mappedBy = "supplierGameMetaData")
  @JsonBackReference
  private Game game;
}
