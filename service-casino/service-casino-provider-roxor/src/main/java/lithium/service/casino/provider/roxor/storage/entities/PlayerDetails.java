package lithium.service.casino.provider.roxor.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class PlayerDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "player_details_id")
    private List<Wins> wins = new ArrayList<>();

    @Column
    private String currentDay;

    @ElementCollection
    @CollectionTable(name = "player_details_days_played", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "days_played")
    private List<String> daysPlayed;

    @Column
    private String dfgPicksRemaining;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable = false)
    private MfgDetails mfgDetails;

    @OneToOne(mappedBy = "playerDetails")
    private Summary summary;

}