package lithium.service.promo.data.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted=false")
public class ChallengeGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = -5827879890215896727L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonBackReference("challengeGroups")
    private PromotionRevision promotionRevision;

    @Builder.Default
    private Boolean sequenced = Boolean.FALSE;

    @Builder.Default
    private Boolean deleted = Boolean.FALSE;

    @JsonManagedReference("challengeGroup")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "challengeGroup")
    private List<Challenge> challenges;

    @Builder.Default
    private Boolean requiresAllChallenges = Boolean.FALSE;

    @Version
    private int version;



    @Override
    public String toString() {
        return new StringJoiner(", ", ChallengeGroup.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("promotionRevision=" + promotionRevision.toShortString())
                .add("challenges=" + challenges)
                .toString();
    }

    public String toShortString() {
        return new StringJoiner(", ", ChallengeGroup.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("promotionRevision=" + promotionRevision.toShortString())
                .toString();
    }
}
