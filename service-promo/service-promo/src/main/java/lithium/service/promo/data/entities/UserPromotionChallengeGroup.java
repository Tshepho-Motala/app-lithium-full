package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPromotionChallengeGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 6461048942027611093L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    private LocalDateTime completed;

    private BigDecimal percentage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_promotion_id")
    @JsonBackReference("userPromotion")
    private UserPromotion userPromotion;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userPromotionChallengeGroup")
    @JsonManagedReference("userPromotionChallengeGroup")
    private List<UserPromotionChallenge> userPromotionChallenges = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "challenge_group_id")
    private ChallengeGroup challengeGroup;

    public void addChallenge(UserPromotionChallenge challenge) {
        userPromotionChallenges.add(challenge);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserPromotionChallengeGroup.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("completed=" + completed)
                .add("percentage=" + percentage)
                .add("userPromotion=" + userPromotion.toShortString())
                .add("challenges=" + userPromotionChallenges)
                .toString();
    }

    public String toShortString() {
        return new StringJoiner(", ", UserPromotionChallengeGroup.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("completed=" + completed)
                .add("percentage=" + percentage)
                .add("userPromotion=" + userPromotion.toShortString())
                .toString();
    }
}
