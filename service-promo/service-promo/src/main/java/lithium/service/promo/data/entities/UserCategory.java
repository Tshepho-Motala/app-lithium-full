package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lithium.service.promo.client.objects.UserCategoryType;
import lithium.service.promo.converter.UserCategoryTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;


@Data
@Entity
@Builder
@ToString(exclude = {"promotionRevision"})
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name="idx_unique_promotion_category", columnList="promotion_revision_id, user_category_id")
})
public class UserCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = -448179302070221100L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "user_category_id")
    private Long userCategoryId;

    @Column(name = "category_type")
    @Convert(converter = UserCategoryTypeConverter.class)
    private UserCategoryType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference("UserCategory")
    @JoinColumn(nullable=false)
    private PromotionRevision promotionRevision;
}
